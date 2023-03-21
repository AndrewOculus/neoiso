all: main.c
	gcc main.c server.c vector.c net.c -Wall -std=c99 -O2 -o server -lpthread -D_POSIX_C_SOURCE=199309L

clean:
	rm server