#!/bin/bash

for i in {0..14}
do {
  { sleep 0; node server;  } &
  { sleep 5; node server;  } &
  { sleep 10; node server; } &
  { sleep 15; node server; } &
  { sleep 20; node server; } &
  { sleep 25; node server; } &
  wait
} done
