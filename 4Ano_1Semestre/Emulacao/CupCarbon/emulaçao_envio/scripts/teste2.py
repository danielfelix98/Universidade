# -*- coding: utf-8 -*-

import random
import time

# Loop
id_recetor = 10
while True:
    c = random.randint(53, 62)
    p = "som: " + str(c)
    print("print {}".format(c), flush=True)
    print("send {} {}".format(c, id_recetor), flush=True)
    time.sleep(1)
