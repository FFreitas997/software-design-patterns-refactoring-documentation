# Chain of Responsibility Pattern

## Intent

Avoid coupling the sender of a request to its receiver by giving **more than one object a chance to handle the request**. Chain the receiving objects and pass the request along the chain until an object handles it.

---

## Also Known As

- CoR, Chain of Command

---

## Motivation

Consider a technical support system. A customer submits a ticket, and it can be handled at different levels:
- **Level 1 (Basic Support)**: handles common, simple issues
- **Level 2 (Technical Support)**: handles more complex technical issues
- **Level 3 (Expert/Manager)**: handles critical or escalated issues

When a request comes in, it starts at Level 1. If Level 1 can't handle it, it passes to Level 2, then Level 3. The client doesn't know which level will ultimately handle it.

This avoids a large if-else chain and allows each handler to be developed, tested, and modified independently.

---

## Applicability

Use Chain of Responsibility when:

- ✅ More than one object may handle a request, and the handler isn't known a priori — the handler should be determined automatically
- ✅ You want to issue a request to one of several objects without specifying the receiver explicitly
- ✅ The set of objects that can handle a request should be specified dynamically

---

## Structure

```
+----------+       +-------------------+
|  Client  |------>|     Handler       |
+----------+       +-------------------+
                   | -successor        |
                   | +setNext(Handler) |
                   | +handle(request)  |
                   +-------------------+
                            ^
               +------------+------------+
               |            |            |
    +----------+--+ +-------+---+ +------+-----+
    |ConcreteHandler1| |ConcreteHandler2| |ConcreteHandler3|
    +----------+--+ +-------+---+ +------+-----+
    |+handle() |   |+handle()  | |+handle()   |
    +----------+   +-----------+ +------------+
```

---

## Participants

- **Handler** — Defines an interface for handling requests; optionally implements the successor link
- **ConcreteHandler** — Handles requests it is responsible for; can access its successor; if it can handle the request, it does so; otherwise it forwards the request to its successor
- **Client** — Initiates the request to a ConcreteHandler object on the chain

---

## Collaborations

When a client issues a request, the request propagates along the chain until a ConcreteHandler handles it or the chain is exhausted (unhandled request).

---

## Consequences

### Benefits
- ✅ **Reduced coupling** — Sender doesn't need to know which handler processes the request
- ✅ **Flexibility in assigning responsibilities** — Handlers can be added or reordered by changing the chain
- ✅ **Single Responsibility** — Each handler only concerns itself with what it can handle

### Drawbacks
- ❌ **Receipt not guaranteed** — A request can fall off the end of the chain without being handled
- ❌ **Hard to debug** — Can be difficult to trace the flow when the chain is long

---

## Related Patterns

- **Composite** — Chain of Responsibility is often used with Composite; in that case, a component's parent can act as its successor
- **Command** — Command can be used in chains; handlers can log/undo requests

---

## Example

See [ChainOfResponsibility.java](ChainOfResponsibility.java) for a complete support ticket system.
