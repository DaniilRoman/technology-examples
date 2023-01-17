FROM alpine:edge as BUILD
RUN apk add --no-cache netcat-openbsd
COPY ./server.sh ./server.sh
RUN chmod +x ./server.sh
ENTRYPOINT [ "./server.sh" ]