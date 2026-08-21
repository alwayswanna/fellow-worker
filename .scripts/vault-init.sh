#!/bin/sh

export VAULT_ADDR=http://vault:8200
INIT_FILE=/vault/data/.init
APPROLE_FILE=/vault/data/.approle

ROLE_NAME=fellow-worker-role
POLICY_NAME=fellow-worker-policy

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
export VAULT_TOKEN="$ROOT_TOKEN"
echo "Root Token: $ROOT_TOKEN"

# ─── KV v2 secrets engine (secret/fellow-worker/*) ─────────────────────────
if ! vault secrets list -format=json | grep -q '"secret/"'; then
  echo "Enabling kv-v2 secrets engine at secret/..."
  vault secrets enable -path=secret -version=2 kv
else
  echo "kv secrets engine already enabled"
fi

# ─── AppRole auth method ────────────────────────────────────────────────────
if ! vault auth list -format=json | grep -q '"approle/"'; then
  echo "Enabling approle auth method..."
  vault auth enable approle
else
  echo "approle auth method already enabled"
fi

# Local dev only: remove the lease cap on the approle mount so that role
# tokens created with token_ttl=0/token_max_ttl=0 never expire.
vault auth tune -default-lease-ttl=0 -max-lease-ttl=0 approle/

# ─── Policy ──────────────────────────────────────────────────────────────
vault policy write "$POLICY_NAME" - <<EOF
path "secret/data/fellow-worker/*" {
  capabilities = ["read", "list"]
}
path "secret/metadata/fellow-worker/*" {
  capabilities = ["read", "list"]
}
EOF

# ─── AppRole role (no TTL — local dev only) ─────────────────────────────────
vault write auth/approle/role/$ROLE_NAME \
  token_policies="$POLICY_NAME" \
  token_ttl=0 \
  token_max_ttl=0 \
  token_num_uses=0 \
  secret_id_ttl=0 \
  secret_id_num_uses=0

ROLE_ID=$(vault read -field=role_id auth/approle/role/$ROLE_NAME/role-id)

if [ ! -f "$APPROLE_FILE" ]; then
  echo "Generating AppRole secret-id..."
  SECRET_ID=$(vault write -f -field=secret_id auth/approle/role/$ROLE_NAME/secret-id)
  {
    echo "VAULT_RID=$ROLE_ID"
    echo "VAULT_SID=$SECRET_ID"
  } > "$APPROLE_FILE"
else
  echo "AppRole secret-id already generated, reusing"
fi

echo "-----------------------------------------"
cat "$APPROLE_FILE"
echo "-----------------------------------------"
