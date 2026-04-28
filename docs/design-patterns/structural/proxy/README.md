# Proxy Pattern

## Intent

Provide a **surrogate or placeholder** for another object to control access to it.

---

## Also Known As

- Surrogate

---

## Motivation

A classic example is a lazy-loading image viewer. A document contains many large images. When the document is opened, not all images need to be loaded immediately — only the ones the user is currently viewing. A proxy (`ImageProxy`) holds the space for the real `Image` object, loading the actual image only when it's first needed (`display()` is called). This makes the document open faster and saves memory.

---

## Types of Proxy

| Type | Description |
|---|---|
| **Virtual Proxy** | Controls access to a resource that is expensive to create. Delays creation until actually needed (lazy initialization). |
| **Protection Proxy** | Controls access based on access rights. Checks permissions before forwarding to the real object. |
| **Remote Proxy** | Represents an object in a different address space. Handles serialization/network calls transparently. |
| **Caching Proxy** | Provides temporary storage for expensive operations. Returns cached results instead of re-executing. |
| **Logging/Auditing Proxy** | Records access to the real object for audit purposes. |
| **Smart Reference** | Performs additional actions when an object is accessed (e.g., reference counting, lock acquisition). |

---

## Applicability

Use Proxy when you need:

- ✅ **Lazy initialization (Virtual Proxy)** — When an object is expensive to create and you don't need it right away
- ✅ **Access control (Protection Proxy)** — When you want only specific clients to be able to use a service
- ✅ **Remote service stub** — When a service object is located on a remote server
- ✅ **Logging requests** — When you want to keep a history of requests to the service object
- ✅ **Caching request results** — When you want to cache results and manage the lifecycle of the cache

---

## Structure

```
+----------+       +-----------+         +----------+
|  Client  |------>|   Subject |         |  RealSubject  |
+----------+       +-----------+         +----------+
                   | +request()|         | +request()|
                   +-----------+         +----------+
                        ^                      ^
                        |                      |
               +--------+-------+              |
               |     Proxy      |--------------+
               +----------------+  (holds reference)
               | -realSubject   |
               | +request()     |
               +----------------+
```

---

## Participants

- **Subject** — Defines the common interface for RealSubject and Proxy, so a Proxy can be used anywhere a RealSubject is expected
- **RealSubject** — Defines the real object that the proxy represents
- **Proxy** — Maintains a reference to the RealSubject; controls access and may be responsible for creating/deleting it; handles various proxy-specific concerns

---

## Consequences

### Benefits
- ✅ **Open/Closed Principle** — Add new proxies without changing service or clients
- ✅ **Transparent to client** — Client doesn't know it's talking to a proxy
- ✅ **Lazy initialization** — Expensive objects are created only when needed
- ✅ **Access control** — Protect sensitive objects

### Drawbacks
- ❌ **Added indirection** — May slow down response time slightly
- ❌ **More code** — More classes and potential maintenance overhead

---

## Related Patterns

- **Adapter** provides a different interface to its subject; Proxy provides the same interface
- **Decorator** adds responsibilities to an object; Proxy controls access to an object
- **Facade** simplifies a subsystem's interface; Proxy is a stand-in for a single object

---

## Example

See [Proxy.java](Proxy.java) for a complete implementation demonstrating a Virtual Proxy and a Protection Proxy.
