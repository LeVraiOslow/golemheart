# GolemHealth

Plugin **Paper 26.2** qui donne aux golems de fer la vie d'un zombie (20 PV, soit 10 cœurs) au lieu de 100 PV.

S'applique aux golems qui apparaissent naturellement ou via un spawner, y compris les spawners stackés de **RoseStacker**.

## Fonctionnalités

- Vie des golems de fer configurable (20 PV par défaut)
- Tous les spawns sont concernés (naturel, spawner, œuf, dispenser...), avec une liste d'exclusion configurable
- Compatible RoseStacker (softdepend)
- Commande de rechargement de la config

## Prérequis

- Serveur Paper 26.2
- Java 25

## Installation

1. Télécharge `GolemHealth.jar` (onglet *Releases* ou *Actions > Artifacts*)
2. Place-le dans le dossier `plugins/` du serveur
3. Redémarre le serveur

## Configuration

`plugins/GolemHealth/config.yml`

```yaml
health: 20.0
debug: false
apply-to-existing: false

excluded-spawn-reasons:
  - BUILD_IRONGOLEM   # golems construits par les joueurs
```

Liste des raisons de spawn : [SpawnReason](https://jd.papermc.io/paper/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html)

## Commandes

| Commande | Permission | Description |
|---|---|---|
| `/golemhealth reload` | `golemhealth.admin` (op par défaut) | Recharge la configuration |

## Compilation

```bash
git clone https://github.com/<ton-pseudo>/GolemHealth.git
cd GolemHealth
mvn clean package
```

Le jar est généré dans `target/GolemHealth.jar`.

Le `pom.xml` utilise la plage de versions `[26.2.build,)` pour récupérer la dernière build de `paper-api` 26.2. Si besoin, fixe une version précise dans la propriété `paper.version` (ex. `26.2.build.7-beta`).

## Notes

- Seuls les nouveaux golems sont modifiés, pas ceux déjà présents dans le monde.
- En cas de souci, active `debug: true` pour voir la raison de spawn de chaque golem dans la console.

## Licence

[MIT](LICENSE)
