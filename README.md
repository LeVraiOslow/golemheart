# GolemHealth

Plugin **Paper 26.2** qui donne aux golems de fer la vie d'un zombie (20 PV, soit 10 cœurs) au lieu de 100 PV.

S'applique aux golems qui apparaissent naturellement ou via un spawner, y compris les spawners stackés de **RoseStacker**.

## Fonctionnalités

- Vie des golems de fer configurable (20 PV par défaut)
- Choix des types de spawn concernés (`NATURAL`, `SPAWNER`, `CUSTOM`...)
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

spawn-reasons:
  - NATURAL
  - SPAWNER
  - CUSTOM
  - VILLAGE_DEFENSE
  - SPAWNER_EGG
  # - BUILD_IRONGOLEM   # golems construits par les joueurs
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

Si Maven ne résout pas `paper-api`, ajuste la propriété `paper.version` dans le `pom.xml` avec la dernière build Paper 26.2.

## Notes

- Seuls les nouveaux golems sont modifiés, pas ceux déjà présents dans le monde.
- Si des golems de spawners RoseStacker gardent 100 PV, ajoute la raison de spawn utilisée dans `spawn-reasons`.

## Licence

[MIT](LICENSE)
