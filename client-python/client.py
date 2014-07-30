#!/usr/bin/env python

import argparse
import time
import socket
import rsa # sudo pip install rsa
import re
import json
import sys

parser = argparse.ArgumentParser()
parser.add_argument("--host", "-a", type=str, default="127.0.0.1")
parser.add_argument("--port", "-p", type=int, default=8192)
parser.add_argument("--service", "-s", type=str, default="votifier-py")
parser.add_argument("--username", "-u", type=str)
parser.add_argument("--voteaddress", type=str, default="127.0.0.1")
parser.add_argument("--timestamp", "-t", type=str, default=str(int(time.time())))
parser.add_argument("--publickey", "-k", type=str)
args = parser.parse_args()

sock = socket.create_connection((args.host, args.port))

def read_line(source):
    line = ""
    while True:
        c = source.recv(1)
        if c == '\n':
            break
        line += c
    return line

version = read_line(sock)

print "Version: '%s'" % version
print "Data:"

data = "VOTE\n%s\n%s\n%s\n%s\n" % (args.service, args.username, args.voteaddress, args.timestamp)

print data

print "Encrypting..."

with open(args.publickey) as f:
    public_key_encoded = f.read()

header = "-----BEGIN PUBLIC KEY-----\n"
footer = "\n-----END PUBLIC KEY-----"
base64_regex = "(" + header + ")?[a-zA-Z0-9+/]+=*(" + footer + ")?"

if not re.match(base64_regex, public_key_encoded):
    public_key_encoded = json.loads(public_key_encoded)["public_key"]
    if not re.match(base64_regex, public_key_encoded):
        print "Invalid key!"
        sys.exit(-1)

if not public_key_encoded[0] == '-':
    public_key_encoded = header + public_key_encoded
if not public_key_encoded[-1] == '-':
    public_key_encoded += footer

public_key = rsa.PublicKey.load_pkcs1_openssl_pem(public_key_encoded)

encrypted = rsa.encrypt(data, public_key)

print "Sending data..."

sock.sendall(encrypted)

remaining = 256 - len(encrypted)
while remaining > 0:
    remaining -= sock.send(chr(0))

sock.close()
