## Kata Architecture Microservice


voir https://github.com/arolla/ChoreographyKata

### Enoncé du problème


Considérons un système de distributions de billets de spectacles en ligne. Le processus de vente consiste typiquement à Réserver (Booking), puis à réduire l'inventaire en correspondance s'il reste suffisamment de place (Inventory), puis à envoyer les billets (Ticketing), chacune de ces étapes étant un service distinct.

*Pour rester simple, chaque service ne fera rien d'autre que prétendre avoir terminé son travail et assurer la coordination d'ensemble.*


Les étapes :

**Approche traditionnelle**

1. Créer chaque service au plus simple (au plus naïf) et les faire s'appeler entre eux pour implémenter le workflow complet. Proposer un refactoring pour simplifier Booking.
1. Extraire la partie orchestration hors du service Booking, dans un nouveau service. Reconnaitre le design pattern classique auquel il correspond.
1. L'acheteur n'est pas informé s'il n'y a plus de place, il faut ajouter une notification (SMS ou email) dans ce cas. Ajouter le nouveau service Notification et faire en sorte q'il soit appelé quand nécessaire.
1. Débrancher le service Notification, observer et commenter les changements nécessaires lors de l'ajout (ou la suppression) de nouveaux services. Proposer une approche alternative.

**Approche alternative**

1. Introduire votre propre EventBus sous forme d'un simple pattern Observer (exemple de code ci-après), refactorer le code pour que toute la coordination se passe au travers du bus, tout d'abord sans le service Notification.
1. Ajouter le service Notification, puis observer et commenter les changements nécessaires lors de l'ajout (ou la suppression) de nouveaux services. Comparer les deux approches, observer comment la logique du workflow est fragmentée dans chaque service. En débrief, donner les avantages et inconvénients respectifs de chaque approche.


```java
/** The listener interface */
public interface Listener {
  void onMessage(Object msg);
}

/**
 * A basic event with a name and one single integer value
 */
public class Event {
  private final String name;
  private final int value;

  public Event(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {return name;}
  public int getValue() {return value;}
}

/**
 * A simple in-memory, observer-pattern-based single-threaded message bus for designing architecture and testing using unit tests before switching to using actual middleware
 */
public class MessageBus {
    private List<Listener> subs = new ArrayList<Listener>();

    public void subscribe(Listener l) {
        this.subs.add(l);
    }

    public void send(String msg) { 
        for (Listener l : subs) {
            l.onMessage(msg);
        }
    }
}
```





### Solution (Spoiler : A lire seulement après avoir fait le kata)

Les services distincts sont les suivants :

```java
public static class BookingService {
  private final OrchestrationService orchestration;

  public BookingService(OrchestrationService orchestration) {
    this.orchestration = orchestration;
  }

  public void bookTicket(int number) {
    System.out.println("Ticket booked");
    orchestration.onTicketBooked(number);
  }
}

public static class InventoryService {
  private int capacity;

  public InventoryService(int initialCapacity) {
    this.capacity = initialCapacity;
  }

  public int onTicketBooked(int number) {
    capacity -= number;
    System.out.println("CapacityChanged:" + capacity);
    return capacity;
  }

  public int currentCapacity() {
    return capacity;
  }
}

public static class TicketingService {
  public void onTicketBooked(int number) {
    System.out.println("Ticket printed for:" + number + " people");
  }
}
```

Dans une approche classique par orchestration, cela pourrait se coder avec un service dédié qui orchestre les 2 autres, lui-même déclenché par le premier :

```java
public static class OrchestrationService {
  private final InventoryService inventory;
  private final TicketingService ticketing;

  public OrchestrationService(InventoryService inventory, TicketingService ticketing) {
    this.inventory = inventory;
    this.ticketing = ticketing;
  }

  public void onTicketBooked(int number) {
    int capacityLeft = inventory.onTicketBooked(number);
    if (capacityLeft >= 0) {
      ticketing.onTicketBooked(number);
    }
  }
}
```

Notez que les services doivent être démarrés dans l'ordre inverse des appels, pour pouvoir injecter leurs références :

```java
@Test
public void orchestration() {
  InventoryService inventory = new InventoryService(3);
  TicketingService ticketing = new TicketingService();
  OrchestrationService orchestration = new OrchestrationService(inventory, ticketing);
  BookingService booking = new BookingService(orchestration);

  assertEquals(3, inventory.currentCapacity());
  booking.bookTicket(1);
  assertEquals(2, inventory.currentCapacity());

}
```

Aussi pour rappel, le `BookingService` connaît le service `OrchestrationService` (injecté en donnée membre), qui lui-même connaît les deux autres, cette fois encore injectés en données membres.

Cette approche est simple et convient tant que la liste des services reste inchangée. En revanche, si on souhaite régulièrement ajouter des services dans le processus, une telle orchestration doit changer à chaque fois.

Par exemple si nous souhaitons ajouter un service de notification en cas de capacité de places insuffisantes, nous devons non seulement ajouter ce nouveau service mais aussi modifier le service `OrchestrationService` à plusieurs endroits pour qu'il l'appelle :

```java
public static class NotificationService {
    public void notifyAlert(String texte) {
      System.out.println("Attention : " + texte);
    }
}

public static class OrchestrationService {
    private final InventoryService inventory;
    private final TicketingService ticketing;
    private final NotificationService notification; // ajout

    public OrchestrationService(InventoryService inventory, 
        TicketingService ticketing,
        NotificationService notification) { // ajout
      this.inventory = inventory;
      this.ticketing = ticketing;
      this.notification = notification;
    }

    public void onTicketBooked(int number) {
      int capacityLeft = inventory.onTicketBooked(number);
      if (capacityLeft >= 0) {
        ticketing.onTicketBooked(number);
      } else {
        notification.notifyAlert("Plus assez de places, désolé !"); // ajout
      }
    }
  }
```

L'alternative à l'orchestration est la chorégraphie, au travers d'un bus de messages qui permet d'informer tous les services de tout changements, sans jamais les connaître au sens des dépendances.

Fondamentalement, l'idée est celle du pattern Observer (GoF). Aussi pour s'exercer avec cette approche nous proposons de la mettre en œuvre en simple code dans un seul process, en représentant le bus de message par un pattern Observer :

```java
/** The listener interface */
public interface Listener {
  void onMessage(Object msg);
}

/**
 * A basic event with a name and one single integer value
 */
public class Event {
  private final String name;
  private final int value;

  public Event(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {return name;}
  public int getValue() {return value;}
}

/**
 * A simple in-memory, observer-pattern-based single-threaded message bus for designing architecture and testing using unit tests before switching to using actual middleware
 */
public class MessageBus {
    private List<Listener> subs = new ArrayList<Listener>();

    public void subscribe(Listener l) {
        this.subs.add(l);
    }

    public void send(String msg) { 
        for (Listener l : subs) {
            l.onMessage(msg);
        }
    }
}
```

Chaque service va s'inscrire au bus, pour être notifié de tout changement, et prend en même temps une référence au bus pour pouvoir aussi lui envoyer des changements.

```java
public static class BookingService implements Listener {
  private final MessageBus bus;

  public BookingService(MessageBus bus) {
    this.bus = bus;
  }

  public void bookTicket(int number) {
    System.out.println("Ticket booked");
    bus.send(new Event("TickedBooked", number));
  }

  @Override
  public void onMessage(Object msg) {
    // ignore all
  }
}
```

```java
public static class InventoryService implements Listener {
    private int capacity;
    private final MessageBus bus;

    public InventoryService(MessageBus bus, int initialCapacity) {
      this.bus = bus;
      this.capacity = initialCapacity;
    }

    @Override
    public void onMessage(Object msg) {
      Event event = (Event) msg;
      //... implémentation à faire ...
    }

    // ... implémentation

    public int currentCapacity() {
      return capacity;
    }
  }
```


```java
public static class TicketingService implements Listener {
    private final MessageBus bus;

    public TicketingService(MessageBus bus) {
      this.bus = bus;
    }

    public void onTicketBooked(int number) {
      System.out.println("Ticket printed for:" + number + " people");
    }

    @Override
    public void onMessage(Object msg) {
      Event event = (Event) msg;
      //...
    }
  }

```

Il est désormais temps d'implémenter le corps des méthodes `onMessage()` selon les événements reçus. L'idée est que tout le code dans le service d’orchestration doit être fragmenté et déplacé dans les méthodes `onMessage()` de chaque service. Puisque l'orchestration est désormais distribuée (au sens de "éparpillée") dans plusieurs services, on parle alors de *chorégraphie*.

```java
public static class InventoryService implements Listener {
    ...

    @Override
    public void onMessage(Object msg) {
      Event event = (Event) msg;
      if (event.getName().equals("TickedBooked")) {
        int number = event.getValue();
        handleTickedBookedEvent(number);
      }
    }

    private void handleTickedBookedEvent(final int number) {
      int left = onTicketBooked(number);
      if (left >= 0) {
        bus.send(new Event("BookingConfirmed", number));
        bus.send(new Event("InventoryChanged", left));
      } else {
        bus.send(new Event("BookingCancelled", 0));
      }
    }
  ...
}
```

Une partie de la logique issue de l'orchestration est clairement reconnaissable, mais elle est cette fois concentrée dans le service d'inventory, qui ne connait pas le service de notification (qui d'ailleurs n'existe pas encore).

Lorsque nous décidons plus tard d'ajouter un service de notification, aucune modification au reste du système n'est nécessaire :

```java
public static class NotificationService implements Listener {
  private final MessageBus bus;

  public NotificationService(MessageBus bus) {
    this.bus = bus;
  }

  public void notifyAlert(String texte) {
    System.out.println("Attention : " + texte);
  }

  @Override
  public void onMessage(Object msg) {
    Event event = (Event) msg;
    if (event.getName().equals("BookingCancelled")) {
      final int number = event.getValue();
      notifyAlert("Plus assez de places, désolé !");
    }
  }
}
```

le diagramme ci-dessous résume cette approche de services qui se partagent un moyen de s'envoyer des événements sans se connaître, pour avoir la possibilité d'ajouter, supprimer ou remplacer des services sans aucune autre modification :

![Un bus partagé par les services de l'exemple, le dernier service en pointillé pour suggérer qu'il peut être ajouté ou supprimé à volonté](Images/growth_ready_architecture.png "Style d'Architecture Microservice")


A noter que c'est désormais de la responsabilité du service de notification de réagir à l'événement signalant le *manque de places dans l'inventaire* en décidant d'émettre une notification, par exemple l'envoi d'un sms.

Autre avantage, les services peuvent se déclarer et s'abonner au bus dans n'importe quel ordre :

```java
@Test
public void choreograph() {
  MessageBus bus = new MessageBus();

  BookingService booking = new BookingService(bus);
  InventoryService inventory = new InventoryService(bus, 3);
  TicketingService ticketing = new TicketingService(bus);
  NotificationService notification = new NotificationService(bus);

  bus.subscribe(booking);
  bus.subscribe(inventory);
  bus.subscribe(ticketing);
  bus.subscribe(notification);

  assertEquals(3, inventory.currentCapacity());
  booking.bookTicket(1);
  assertEquals(2, inventory.currentCapacity());
}
```

Et si le bus a la propriété d'être *durable*, alors les services peuvent être éteints puis re-démarrés indépendamment, tandis que les événements qu'ils doivent recevoir restent en attente sur le bus, prêts à être traités dès qu'un service redevient disponible.

Au travers de cet exemple simpliste, les avantages et inconvénients  respectifs des approches de chorégraphie ou d'orchestration apparaissent clairement :

- Orchestration : le processus complet est centralisé dans l'orchestrateur, qui est donc facile à auditer, et qui offre des bonnes garanties sur l'intégrité du  processus complet. Tout cela se fait au dépend de devoir changer l'orchestration à chaque ajout ou suppression d'un service.

- Choregraphie : L'ajout, le remplacement ou la suppression des services peut s'effectuer sans aucun changement à effectuer dans un autre endroit du système. Mais cela se fait au dépend d'une plus grande difficulté à comprendre ou vérifier le processus complet.

Le style d'architecture orienté microservices, avec son architecture Event-Driven sur un bus de message et la préférence pour la chorégraphie, permettent fondamentalement d'appliquer le principe Open-Close (OCP), à l'échelle des services que l'on peut ajouter et retirer sans aucun autre impact, conformément à l'idée de déploiement totalement indépendant des services. 



