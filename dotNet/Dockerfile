FROM mono:6.12

LABEL org.opencontainers.image.authors="Philippe Collet philippe.collet@univ-cotedazur.fr"

# Loading the executable server inside the image
COPY ./server.exe ./server.exe

# exposing the 9090 port to support external connections
EXPOSE 9090

# Running the .Net server as standalone
ENTRYPOINT ["mono", "./server.exe"]
CMD ["/standalone"]
