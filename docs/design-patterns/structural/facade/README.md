# Facade Pattern

## Intent

Provide a **simplified interface** to a complex subsystem.

---

## Also Known Also

- Front Controller (loosely related)

---

## Motivation

Structuring a system into subsystems helps reduce complexity. A common design goal is to minimize communication and dependencies between subsystems. One way to achieve this is to introduce a **Facade** object that provides a single, simplified interface to the more general facilities of a subsystem.

Consider a home theater system with many components: Amplifier, Tuner, DVD Player, CD Player, Projector, Screen, Lights, Popcorn Machine. To watch a movie, a user must:
1. Turn on the popcorn machine
2. Start popping
3. Dim the lights
4. Put down the screen
5. Turn on the projector, set to widescreen
6. Turn on the amplifier, set to DVD input, set volume
7. Turn on the DVD player, play movie

That's a lot of steps. A `HomeTheaterFacade.watchMovie()` method can do all of this in one call, while still allowing direct access to each subsystem for advanced use.

---

## Applicability

Use Facade when:

- ✅ You want to provide a **simple interface** to a complex subsystem
- ✅ There are many dependencies between clients and the implementation classes of an abstraction — Facade decouples the subsystem from clients
- ✅ You want to **layer your subsystems** — use a Facade to define an entry point to each subsystem level
- ✅ You want to wrap a poorly designed collection of APIs with a single well-designed API

---

## Structure

```
+----------+      +------------------+
|  Client  |----->|     Facade       |
+----------+      +------------------+
                  | +watchMovie()    |
                  | +endMovie()      |
                  +------------------+
                         |
          +--------------+--------------+
          |              |              |
   +------+------+ +-----+-----+ +------+------+
   | Amplifier   | |  Projector| |   Lights    |
   +-------------+ +-----------+ +-------------+
   | DVDPlayer   | |  Screen   | | Popcorn     |
   +-------------+ +-----------+ +-------------+
```

---

## Participants

- **Facade** (`HomeTheaterFacade`) — Knows which subsystem classes are responsible for a request and delegates requests to appropriate subsystem objects
- **Subsystem Classes** (`Amplifier`, `DVDPlayer`, `Projector`, etc.) — Implement subsystem functionality; handle work assigned by the Facade; have no knowledge of the facade

---

## Collaborations

- Clients communicate with the subsystem by sending requests to Facade
- Facade forwards the requests to the appropriate subsystem objects
- Clients that use the Facade don't have to access subsystem objects directly (but can if needed)

---

## Consequences

### Benefits
- ✅ **Shields clients** from subsystem components, reducing the number of objects clients deal with
- ✅ **Promotes weak coupling** between subsystem and clients — allows changing subsystem components without affecting clients
- ✅ **Doesn't prevent advanced use** — Clients can still access subsystem classes directly if needed
- ✅ **Layered architecture** — Each layer gets a Facade as an entry point

### Drawbacks
- ❌ **Can become a "god object"** — If the Facade does too much, it can accumulate logic that doesn't belong to it
- ❌ **Risk of tight coupling to Facade** — Clients may become coupled to the Facade itself

---

## Related Patterns

- **Abstract Factory** can be used with Facade to provide an interface for creating subsystem objects
- **Mediator** is similar to Facade: both abstract functionality of existing classes. Mediator centralizes communication *between* colleagues; Facade simplifies access *to* a subsystem
- **Singleton** — Facade objects are often Singletons
- **Adapter** wraps one class; Facade wraps an entire subsystem

---

## Example

See [Facade.java](Facade.java) for a complete home theater system implementation.
