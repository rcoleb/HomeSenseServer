import machine
import socket
import time
import urequests
import network
import json
import ssl
from machine import Pin

ssid = '' # TODO set
passw = '' # TODO set

rmq_q = 'device_event_stream'
rmq_h = '' # TODO set
rmq_p = 5672
rmq_user = '' # TODO set
rmq_pass = '' # TODO set
rmq_e = 'amq.direct'
rmq_r = 'all'

deviceId = 1
deviceType = 'WASHER'

reed = Pin(16, Pin.IN)
vibe = Pin(20, Pin.IN)

reed_buffer_size = 10
vibe_buffer_size = 600

reed_threshold = 0.5
reed_hysteresis = 2

vibe_threshold = 0.2
vibe_threshold_off = 0
vibe_hysteresis = 0

reed_closed = False
vibe_running = False

def connect():
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)
    wlan.connect(ssid, passw)
    while wlan.isconnected() == False:
        print('Waiting for connection...')
        time.sleep(1)
    ip = wlan.ifconfig()[0]
    print(f'Connected on {ip}')
    return ip

def rmq_send(eventType):
    t1 = time.localtime()
    t = "{}-{:02d}-{:02d} {:02d}:{:02d}:{:02d}".format(t1[0], t1[1], t1[2], t1[3], t1[4], t1[5])
    loc = "http://" + rmq_h + ":15672/api/exchanges/%2f/" + rmq_e + "/publish"
    d = '{"properties":{"delivery_mode":2},"routing_key":"all","payload":"{\\"eventType\\": \\"' + eventType + '\\", \\"deviceId\\": ' + str(deviceId) +', \\"timestamp\\": \\"' + t + '\\"}","payload_encoding":"string"}'
    print(d)
    resp = urequests.post(loc, headers = {'content-type': 'application/json'}, data = d, auth = (rmq_user, rmq_pass))
    print(resp.status_code)
    print(resp.text)
    print(resp.content)

try:
    ip = connect()

    # register device
    data1 = {"id": deviceId, "deviceType": deviceType}
    urequests.post("http://" + rmq_h + ":8181/device", headers = {'content-type': 'application/json'}, data=json.dumps(data1))

    reed_buff = [0] * reed_buffer_size
    vibe_buff = [0] * vibe_buffer_size

    while True:

        reed_buff.append(reed.value())
        vibe_buff.append(vibe.value()^1)
        reed_buff.pop(0)
        vibe_buff.pop(0)

        reed_rms = (sum([x*x for x in reed_buff])/reed_buffer_size)**0.5
        vibe_rms = (sum([x*x for x in vibe_buff])/vibe_buffer_size)**0.5

        if vibe_rms > vibe_threshold and not vibe_running:
            for i in range(vibe_hysteresis):
                if vibe.value()^1 < vibe_threshold:
                    break
                time.sleep(0.1)
            else:
                # send door closed event
                vibe_running = True
                rmq_send('STARTED')
                print('Started')

        elif vibe_rms <= vibe_threshold and vibe_running:
            for i in range(vibe_hysteresis):
                if vibe.value()^1 > vibe_threshold:
                    break
                time.sleep(0.1)
            else:
                vibe_running = False
                rmq_send('STOPPED')
                print('Stopped')

        if reed_rms > reed_threshold and not reed_closed:
            for i in range(reed_hysteresis):
                if reed.value() < reed_threshold:
                    break
                time.sleep(0.1)
            else:
                # send door closed event
                reed_closed = True
                rmq_send('DOOR_CLOSED')
                print('Door Closed')

        elif reed_rms <= reed_threshold and reed_closed:
            for i in range(reed_hysteresis):
                if reed.value() > reed_threshold:
                    break
                time.sleep(0.1)
            else:
                reed_closed = False
                rmq_send('DOOR_OPENED')
                print('Door Opened')

        time.sleep(0.1)

except KeyboardInterrupt:
    machine.reset()
