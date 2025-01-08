# Command Block Scopes

Save command block placer and execute command with the command block placer.

## Config (v1.2~)

```yaml
dataSaveTo: "customblockdata"
```

dataSaveTo allows:

- customblockdata: (default) save data to [CustomBlockData](https://github.com/mfnalex/CustomBlockData)
- memory: (limited scene) save data to memory. if server restarted, data will be lost
- originalformat: (experimental) save data with original format
