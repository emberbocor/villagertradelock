# Villager Trade Lock

Break and replace a job site block without losing a villager's trades.

Villager Trade Lock gives every villager a memory of its trades per profession. When a villager loses its profession, it remembers the trades it had. When it takes the same profession again, from any job site block, it gets exactly those trades back instead of a new random set. A profession it has never had still rolls its trades the vanilla way.

The mod runs on the server only. Players join with a plain vanilla client and see the remembered trades in the normal trading screen.

Available for NeoForge, Forge and Fabric.

## Features

- **Trades survive a lost job.** Breaking a villager's job site block no longer throws its trades away. Give it the same kind of block again, here or anywhere else, and the old trades come back.
- **One memory per profession.** A villager that goes from librarian to armorer and back to librarian gets its librarian trades back. Its armorer trades are rolled once and remembered too.
- **Professions from other mods.** The memory is keyed by profession, so professions added by other mods are remembered the same way.
- **Survives zombies.** A villager that is turned into a zombie villager and cured keeps its memory.
- **Works with Villager Trade Peek.** When Villager Trade Peek is installed, the previewed future trades are remembered and restored as well, so the preview does not change either.
- **Prices stay vanilla.** Reputation, discounts, demand and Hero of the Village work exactly as in vanilla.

There is no config file.

## How it works

- **When the memory is taken.** The moment a villager loses its profession, the trades it holds at that moment are stored for that profession, replacing anything stored for it before.
- **When the memory is used.** The moment a villager takes a profession it has a memory for, those trades become its trades and vanilla does not roll new ones. For a profession without a memory, vanilla rolls the trades as usual, and they are stored the next time the villager loses that profession.
- **Only villagers that have never traded.** In vanilla, a villager loses its profession only while it has no trading experience and is still a Novice. Once someone has traded with it, it keeps its profession and its trades for good, with or without this mod. So the mod only ever affects villagers that nobody has traded with yet.
- **Unemployed villagers and nitwits.** They have no trades, so there is nothing to remember.
- **Zombie villagers.** The memory moves to the zombie villager and back to the villager when it is cured, like the villager's own trades and reputation do.
- **Existing worlds.** Villagers that already exist are covered as soon as the mod is installed. Their memory fills up the first time they lose a profession afterwards.

## Requirements

| | Forge / NeoForge | Fabric |
|---|---|---|
| Minecraft | 1.20.1 | 1.20 or 1.20.1 |
| Loader | Forge 47.1.0 or newer, or NeoForge 1.20.1 (47.1.x) | Fabric Loader 0.14.21 or newer |
| Jar | `villagertradelock-forge-1.20.1-<version>.jar` | `villagertradelock-fabric-1.20-1.20.1-<version>.jar` |

On Minecraft 1.20.1, NeoForge still runs Forge mods, so the Forge jar is used for both.

The Fabric version does not need Fabric API.

### Client and server

Install the mod on the server only, on every loader. It adds no items, blocks, entities, network messages or registries, so the server has nothing a client needs to know about:

- **Forge and NeoForge:** vanilla clients and Forge or NeoForge clients without the mod can join. The mod is marked as server-only, so a client without it still shows the server as compatible in the server list.
- **Fabric:** vanilla clients and Fabric clients without the mod can join.

Installing it on a client as well does no harm.

In singleplayer, install it in your own game; the built-in server runs it.

## Removing the mod

The world opens normally. The memory is stored on each villager under the mod's own key, and vanilla ignores it. Villagers keep their current trades, and from then on a villager that loses its profession rolls new trades the vanilla way.

## Compatibility

- **Villager Trade Peek:** optional, no extra setup. When it is installed, the future trades it previews are stored together with the Novice trades and restored with them, so the preview stays the same after a job site block is broken and replaced, or after a villager switches professions and comes back. Its preview is only remembered once a player has opened the villager's trading screen, because Villager Trade Peek creates it at that point. Without Villager Trade Peek, this part simply does nothing.
- **Villager Trade Swap:** works as is. Swapped trades are part of the villager's trades, so a villager that loses its profession after a swap remembers the swapped trades and gets them back.
- **Villager Cure Discounts:** works as is. Villager Trade Lock does not touch reputation or prices, so cure discounts apply to remembered trades exactly as to any other trades.
- **Mods that change how trades are picked:** they still decide the trades a villager gets for a profession it has never had. Once a profession is remembered, the remembered trades come back instead. Mods that reroll a villager's trades without changing its profession are not affected by the memory.

## Building from source

```
./gradlew build
```

The jars are written to `forge/build/libs/` and `fabric/build/libs/`.

## License

MIT.
