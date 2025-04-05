# Project xx1: Spring Security with self-signed JWT token.
## 🔐 JWT Key Setup

This application issues self-signed JWTs using an RSA key pair.

### 1. Generate RSA Key Pair on your machine

```bash
# Generate private key (2048-bit RSA)
openssl genrsa -out keypair.pem 2048

# Extract public key
openssl rsa -in keypair.pem -pubout -out public.pem

# Convert private key to PKCS#8 (required for Java)
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem

# delete the keypair.pem and keep both the private and public pem files.
