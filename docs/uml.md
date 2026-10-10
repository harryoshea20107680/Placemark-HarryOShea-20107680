```mermaid

classDiagram
direction TB

    class PlacedMark {
        +Long id
        +String title
        +String desc
        +Double x
        +Double y
    }

    class PlacemarkStore {
        <<interface>>
        +findAll() List~PlacedMark~
        +create(placemark)
        +update(placemark) Boolean
        +delete(id) Boolean
        +findOne(id) PlacedMark
    }

    class PlacemarkMemStore {
        -ArrayList~PlacedMark~ placemarks
        -AtomicLong lastId
        +findAll() List~PlacedMark~
        +create(placemark)
        +update(placemark) Boolean
        +delete(id) Boolean
        +findOne(id) PlacedMark
    }

    PlacemarkStore <|.. PlacemarkMemStore
    PlacemarkMemStore o-- PlacedMark
  ```