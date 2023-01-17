#!/bin/sh
HTML_CONTENT="<!DOCTYPE html><html><head><title>Simple Netcat Server</title></head><body><h1>TTTest</h1><h2>$ENV_VAR</h2></body></html>"

while true; do echo -e "HTTP/1.1 200 OK\n\n$HTML_CONTENT" | nc -l -w 1 8080; done