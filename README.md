# 🍀 Lucky Block Mod

A simple but powerful **(Infinite) Lucky Block** mod for Fabric Minecraft 1.21.10.

Breaking a Lucky Block triggers random events — from harmless gifts to chaotic disasters.  
For the brave, there is also the **(Infinite) Super Lucky Block**, with a higher risk–reward balance.

**Infinite** because the lucky blocks respawn when they are destroyed, when not mined with the correct too.

---

## How to use

- **Normal Lucky Block** and **Super Lucky Block** can only be mined using a **Netherite Hoe**.
- When broken with any other tool or the bare hand, the block:
  1. Rolls a random **Lucky Category** (see below).
  2. Executes a corresponding **event** or **item drop**.
  3. Respawns automatically after the event.

---

## Luck Categories and Probabilities

Each block type uses its own weighted distribution for the **Luck Categories**.  
These categories define whether the outcome will be good, neutral, or bad — but not the exact event.

### Lucky Block

| Category | Chance |
|-----------|---------|
| VERY UNLUCKY | 1 % |
| UNLUCKY | 19 % |
| COMMON | 70 % |
| LUCKY | 8 % |
| VERY LUCKY | 2 % |

---

### Super Lucky Block

| Category | Chance |
|-----------|---------|
| VERY UNLUCKY | 6 % |
| UNLUCKY | 35 % |
| COMMON | 23 % |
| LUCKY | 19 % |
| VERY LUCKY | 7 % |

The **Super Lucky Block** tends to generate stronger rewards but also far more dangerous unlucky outcomes.

---

## Statistics

Every time a player breaks a Lucky Block (of any type), the mod tracks:
- How many blocks they have broken in total.
- How many outcomes per **Luck Category** they have triggered.

You can view stats using: 

- `/luckyblock stats` (will print stats for the player that issues the command)
- `/luckyblock stats <player>`
- `/luckyblock stats all`

---

## Notes

- The mod tracks player stats in-memory (they reset when the server restarts).
- Only `Netherite Hoe` works as a valid mining tool.
- The loot tables and events are internally balanced — item probabilities are not exposed here to keep the surprise.

---

## Credits

Developed by Lennart Lutz.




