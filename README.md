# demoapp-rsa-sign

## Java Keytool

### Create Keystore with Keytool
```
$ keytool -genkey -alias server -keyalg RSA -keystore server.jks -keysize 2048 -validity 3650 \
    -dname "CN=Server, OU=Backend, O=AutoGrivity, L=Irvine, S=California, C=US" \
    -storepass password -keypass password

$ keytool -genkey -alias partner -keyalg RSA -keystore partner.jks -keysize 2048 -validity 3650 \
    -dname "CN=Partner, OU=Partner Unit, O=Partner Inc, L=Irvine, S=California, C=US" \
    -storepass password -keypass password
```

### Export Certificate from Keystore
```
$ keytool -export -alias server -keystore server.jks -file server.cer
$ keytool -export -rfc -alias server -keystore server.jks -file server.crt

$ keytool -export -alias partner -keystore partner.jks -file partner.cer
$ keytool -export -rfc -alias partner -keystore partner.jks -file partner.crt
```

### Export Private Key from Keystore

Export from keytool's proprietary format (called "JKS") to standardized format PKCS #12:
```
$ keytool -importkeystore -srckeystore server.jks -destkeystore server.p12 \
    -deststoretype PKCS12 -srcalias server -deststorepass password -destkeypass password

$ keytool -importkeystore -srckeystore partner.jks -destkeystore partner.p12 \
    -deststoretype PKCS12 -srcalias partner -deststorepass password -destkeypass password
```

Export certificate using openssl:
```
$ openssl pkcs12 -in server.p12 -nokeys -out server.crt

$ openssl pkcs12 -in partner.p12 -nokeys -out partner.crt
```

Export unencrypted private key:
```
$ openssl pkcs12 -in server.p12 -nodes -nocerts -out server.key

$ openssl pkcs12 -in partner.p12 -nodes -nocerts -out partner.key
```

## OpenSSL

### Create Certificates with OpenSSL
```
$ openssl req -x509 -newkey rsa:2048 -keyout server.key -out server.crt -days 3650 -nodes
```

### View Certificates
```
$ openssl x509 -in server.crt -text -noout
$ openssl x509 -in server.cer -inform der -text -noout
```

### Convert PEM to DER
```
$ openssl x509 -outform der -in server.crt -out server.cer
$ openssl rsa -outform der -in server.pem -out server.der
```

### Convert DER to PEM
```
$ openssl x509 -inform der -in server.cer -out server.crt
$ openssl rsa -outform der -in server.der -out server.pem
```

### To remove the Bag Attributes
```
$ openssl x509 -in server.crt -out server.crt
$ openssl rsa -in server.key -out server.key
```
