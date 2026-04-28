# Command Pattern

## Intent

**Encapsulate a request as an object**, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.

---

## Also Known As

- Action, Transaction

---

## Motivation

A graphical text editor has a toolbar, menu, and keyboard shortcuts — all doing the same operations (cut, copy, paste, undo). A naive approach wires each button directly to the editor. This creates tight coupling: buttons know about the editor's internals.

The Command pattern extracts each operation into a separate `Command` object with an `execute()` method. The button/menu item stores a command reference and just calls `execute()`. This:
- Decouples the UI from the business logic
- Allows operations to be queued, logged, or undone
- Lets you build a macro system by combining commands
- Supports undo by keeping a history of executed commands

---

## Applicability

Use Command when:

- ✅ You want to **parameterize objects** with operations (e.g., toolbar buttons with arbitrary actions)
- ✅ You want to **specify, queue, and execute requests** at different times
- ✅ You need to support **undo/redo** operations
- ✅ You want to support **logging changes** so they can be reapplied after a system crash
- ✅ You want to structure a system around **high-level operations** built on primitives (macro commands)

---

## Structure

```
+----------+         +-----------+         +-----------+
|  Client  |-------->|  Invoker  |-------->|  Command  |
+----------+         +-----------+         +-----------+
                     |+setCommand|         |+execute() |
                     |+execute() |         |+undo()    |
                     +-----------+         +-----------+
                                                 ^
                                     +-----------+-----------+
                                     |                       |
                            +--------+------+       +--------+------+
                            |ConcreteCmd A  |       |ConcreteCmd B  |
                            +---------------+       +---------------+
                            |receiver: R    |       |+execute()     |
                            |+execute()     |       |+undo()        |
                            |+undo()        |       +---------------+
                            +---------------+
```

---

## Participants

- **Command** — Declares an interface for executing an operation (and optionally undoing it)
- **ConcreteCommand** — Implements `execute()` by invoking operations on the Receiver; stores state for undoing
- **Client** — Creates a ConcreteCommand object and sets its receiver
- **Invoker** — Asks the command to carry out the request; may keep a command history
- **Receiver** — Knows how to perform the operations associated with carrying out a request

---

## Collaborations

1. The Client creates a ConcreteCommand and specifies its Receiver
2. An Invoker object stores the ConcreteCommand
3. The Invoker calls `execute()` on the command
4. The ConcreteCommand invokes operations on the Receiver to carry out the request
5. For undo, the ConcreteCommand restores the state that was saved before `execute()`

---

## Consequences

### Benefits
- ✅ **Decouples** invoker from the object that performs the action
- ✅ **Commands are first-class objects** — can be manipulated and extended
- ✅ **Composite Commands** — assemble commands into macro commands
- ✅ **Easy undo/redo** — store command history; call `undo()` in reverse
- ✅ **Deferred execution** — queue commands for later execution

### Drawbacks
- ❌ **Lots of small classes** — Each command is a separate class
- ❌ **Memory overhead** — Keeping command history uses memory

---

## Related Patterns

- **Composite** can be used to implement MacroCommands
- **Memento** can keep state the command needs to undo its effect
- **Prototype** can be used to create command copies for the undo history
- **Chain of Responsibility** routes commands; Command encapsulates requests

---

## Example

See [Command.java](Command.java) for a complete text editor with undo/redo support.
