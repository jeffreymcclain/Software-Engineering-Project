#!/usr/bin/env python

#Note: requires websockets installed via pip install websockets
#Note: async requires python >=3.5

import asyncio
import signal
import websockets

async def echo(websocket, path):
    while True:
        try:
            print("Serving")
            msg = await websocket.recv()
        except websockets.ConnectionClosed:
            pass
        else:
            await websocket.send(msg)

async def echo_server(stop):
    async with websockets.serve(echo, 'localhost', 8765):
        await stop

loop = asyncio.get_event_loop()

# The stop condition is set when receiving SIGTERM.
stop = asyncio.Future()
#loop.add_signal_handler(signal.SIGTERM, stop.set_result, None)

# Run the server until the stop condition is met.
loop.run_until_complete(echo_server(stop))