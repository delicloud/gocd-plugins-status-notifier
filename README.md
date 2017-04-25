GoCD build status notifier plugins
=================================

- Notify Deliflow cd-metrics service about [`stage status changed`](https://plugin-api.gocd.io/current/notifications/#stage-status-changed) event


## Requirements

These plugins require GoCD version >= v15.x or above

## Get Started

### Installation:

Download the latest plugin jar from Releases section. Place it in <go-server-location>/plugins/external & restart Go Server.

## Configuration

> todo: Deliflow wants to handle multi gocd server, but that makes it very difficult to identify which server's data is pushing to cd-metrics. For now, this plugin use username/password combination to call gocd API to get server's UUID. But this approach is not elegent. 
