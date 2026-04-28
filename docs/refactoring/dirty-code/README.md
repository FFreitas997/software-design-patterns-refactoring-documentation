# Dirty Code

## What is Dirty Code?

**Dirty code** (also called *messy code*, *legacy code*, or *spaghetti code*) is source code that works functionally but is difficult to read, understand, modify, or extend. It is code written without consistent structure, clear naming, or design principles — often produced under time pressure, by inexperienced developers, or simply through years of accumulated shortcuts and quick fixes.

Dirty code is not necessarily broken code. It may pass all its tests and deliver correct results. The damage is **economic and temporal**: it slows down every future developer who has to work with it.

> *"Any fool can write code that a computer can understand. Good programmers write code that humans can understand."*
> — Martin Fowler

---

## Signs of Dirty Code

### 🍝 Spaghetti Code
Control flow that jumps around unpredictably — deeply nested `if/else` blocks, loops with multiple `break`/`continue` statements, and methods that do wildly different things depending on invisible state. Following the logic feels like untangling a bowl of spaghetti.

### 🏷️ Bad Naming
- Variables named `a`, `b`, `x`, `temp`, `data`, `obj`, `thing`
- Methods named `doStuff()`, `process()`, `handle()`, `calc()`
- Classes named `Manager`, `Processor`, `Helper` that could mean anything
- Abbreviations that save three keystrokes but cost ten minutes of confusion (`usrNm`, `cstmrLst`, `calcTtl`)

### 🏗️ No Structure / No Separation of Concerns
Everything in one giant class or one giant method. Business logic, database access, input validation, and output formatting all tangled together. No layers, no boundaries, no cohesion.

### 📋 Code Duplication
The same logic copy-pasted into three different places. When the requirement changes, all three copies must be found and updated — and inevitably one is missed, causing a bug.

### 🔢 Magic Numbers and Hardcoded Values
```java
// What does 86400 mean? What does 3 mean?
if (sessionAge > 86400) {
    status = 3;
}
```
Numbers and strings embedded directly in logic with no explanation. When the business rule changes, you have to hunt through thousands of lines to find every occurrence.

### 📏 Long Methods
Methods that span hundreds of lines doing dozens of things. The longer a method is, the harder it is to understand, test, and reuse any part of it.

### 🐘 Large Classes (God Classes)
A single class that knows everything and does everything. It accumulates fields and methods over time until it becomes the center of the universe — impossible to test in isolation and coupled to half the codebase.

### 🔗 Deep Nesting
```java
if (user != null) {
    if (user.isActive()) {
        if (user.hasPermission("READ")) {
            if (resource != null) {
                // actual logic buried 4 levels deep
            }
        }
    }
}
```
Code indented so deeply that the actual logic is a small island in a sea of braces.

### 💬 Misleading or Outdated Comments
Comments that describe what the code used to do, not what it does now. Or comments that explain *what* the code does rather than *why* — which a reader could see by reading the code.

### 🔀 Inconsistency
Different conventions in different parts of the codebase. Some methods use camelCase, others use snake_case. Some classes throw checked exceptions, others return null. No predictable pattern.

---

## Consequences of Dirty Code

### 💳 Technical Debt
Every piece of dirty code is a loan against future productivity. Like financial debt, it accrues interest. The longer it is left unaddressed, the more time every future change costs.

### 🐛 Bugs Multiply
Poorly structured code hides bugs and makes new ones easy to introduce. When logic is duplicated, a bug fixed in one place reappears in another. When methods are long and tangled, side effects are unpredictable.

### 🐢 Development Slows Down
New features take longer because developers must first decode the existing code. Estimates become unreliable. Simple changes require touching many files. Fear of breaking things leads to hesitation.

### 😓 Developer Morale Suffers
Working in a dirty codebase is demoralizing. Developers feel like archaeologists digging through someone else's mess rather than engineers building something valuable.

### 🚫 Hard to Test
Dirty code with tight coupling and no separation of concerns is nearly impossible to unit test. Without tests, every change is a gamble.

---

## Example

See [DirtyCodeExample.java](./DirtyCodeExample.java) for a concrete Java illustration of multiple dirty code anti-patterns.

Compare it with [CleanCodeExample.java](../clean-code/CleanCodeExample.java) to see the same functionality written cleanly.
