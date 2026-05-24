#!/bin/sh

export VAULT_ADDR=http://vault:8200
INIT_FILE=/vault/data/.init

echo "Waiting for Vault to start..."
while true; do
  vault status > /dev/null 2>&1
  [ $? -ne 1 ] && break
  sleep 2
done

echo "Vault is reachable"

if [ ! -f "$INIT_FILE" ]; then
  echo "Initializing Vault..."
  if ! vault operator init -key-shares=1 -key-threshold=1 > "$INIT_FILE" 2>&1; then
    echo "ERROR: Vault initialization failed:"
    cat "$INIT_FILE"
    rm -f "$INIT_FILE"
    exit 1
  fi
  echo "Vault initialized"
fi

vault status > /dev/null 2>&1
if [ $? -ne 0 ]; then
  UNSEAL_KEY=$(awk '/Unseal Key 1:/ {print $NF}' "$INIT_FILE")
  if [ -z "$UNSEAL_KEY" ]; then
    echo "ERROR: Could not read unseal key from $INIT_FILE"
    cat "$INIT_FILE"
    exit 1
  fi
  echo "Unsealing Vault..."
  vault operator unseal "$UNSEAL_KEY"
  echo "Vault unsealed"
else
  echo "Vault already unsealed"
fi

ROOT_TOKEN=$(awk '/Initial Root Token:/ {print $NF}' "$INIT_FILE")
echo "Root Token: $ROOT_TOKEN"