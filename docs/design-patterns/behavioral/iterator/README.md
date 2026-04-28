# Iterator Pattern

## Intent

Provide a way to **access elements of an aggregate object sequentially** without exposing its underlying representation.

---

## Also Known As

- Cursor

---

## Motivation

A list object can be traversed in different ways: forwards, backwards, only certain elements, randomly, etc. You don't want to bloat the list's interface with all these traversal algorithms. Nor do you want client code to know the internal structure (is it an array? a linked list? a tree?).

The Iterator pattern extracts the traversal algorithm into a separate `Iterator` object. The client talks to the iterator, not directly to the collection. The same collection can have multiple iterators active simultaneously, each maintaining its own traversal position.

This is so useful that Java built it into the language with `java.lang.Iterable` and `java.util.Iterator`.

---

## Applicability

Use Iterator when:

- ✅ You want to access an aggregate's contents without exposing its internal representation
- ✅ You want to support **multiple simultaneous traversals** of aggregates
- ✅ You want to provide a **uniform interface** for traversing different aggregate structures (polymorphic iteration)

---

## Structure

```
+-------------+         +-----------+
|   Client    |-------->| Aggregate |
+-------------+         +-----------+
      |                 |+createIterator()|
      |                 +-----------+
      |                       ^
      v                       |
+-----------+       +---------+---------+
| Iterator  |       |ConcreteAggregate  |
+-----------+       +-------------------+
|+first()   |       |items              |
|+next()    |       |+createIterator()  |
|+hasNext() |       +-------------------+
|+current() |
+-----------+
      ^
      |
+-----+--------+
|ConcreteIterator|
+----------------+
|index           |
|+first()        |
|+next()         |
|+hasNext()      |
|+current()      |
+----------------+
```

---

## Participants

- **Iterator** — Defines an interface for accessing and traversing elements
- **ConcreteIterator** — Implements the Iterator interface; keeps track of current position
- **Aggregate** — Defines an interface for creating an Iterator object
- **ConcreteAggregate** — Implements the Iterator creation interface; returns an instance of the proper ConcreteIterator

---

## Collaborations

A ConcreteIterator keeps track of the current object in the aggregate and can compute the succeeding object in the traversal.

---

## Consequences

### Benefits
- ✅ **Multiple traversals** — Multiple iterators can traverse the same aggregate simultaneously
- ✅ **Simplified aggregate interface** — Traversal code moves out of the aggregate
- ✅ **Polymorphic iteration** — Write code that can iterate over any type of aggregate

### Drawbacks
- ❌ **Concurrent modification** — If the aggregate is modified during iteration, results may be unpredictable (need to handle `ConcurrentModificationException` or use a snapshot iterator)
- ❌ **Overhead** — For simple arrays, a direct for-loop is simpler

---

## Related Patterns

- **Composite** — Iterators are often applied to composites
- **Factory Method** — Polymorphic iterators rely on factory methods to instantiate the appropriate Iterator
- **Memento** — An Iterator can use a memento to save the state of an iteration

---

## Example

See [Iterator.java](Iterator.java) for a complete custom collection with multiple iterator types.
