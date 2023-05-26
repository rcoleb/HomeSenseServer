
# HomeSenseServer Project Writeup

Bear with me, as this is the first writeup of its kind that I've ever done!

## Context

I live on the third floor of a three-story triplex. My friends live on floors one and two. 

## The Problem

The laundry machines are in the basement, down four flights of stairs. Sometimes I'll bring my laundry downstairs to find that the machines are already in use, meaning I have to estimate how long the machines have left to run, and then make an extra trip up and down the four flights of stairs. If I've estimated incorrectly, I might end up making even more trips up and down. 

This is a lot of stairs. 

These are not smart appliances. I'm not a huge fan of smart appliances, for reasons. So I can't just check an app on my smartphone to see if the washer or dryer are free to use. 

But what if I could?

## The Solution

I have a Raspberry Pi 3B+ that I'm not using. I bought it a few years ago thinking I would do something with it ... I have not yet done anything with it.

Because washers and dryers are closed devices, I can't really insert anything into them to detect if they're running or not. A thermal sensor might work for a dryer, but would have a fair bit of delay as it takes a while to come up to temp, and a longer while to cool off.

Some sort of power-draw detector might work? But that's a bit more involved, as there isn't an off-the-shelf pass-through electrical circuit power-draw sensor for the Raspberry Pi system (that I'm aware of). 

So I settled on vibration sensors - I think both the washer and dryer have enough vibration when running that I could detect those vibrations with some sort of sensor.

There are two vibration sensors available for the Raspberry Pi system:
 - The SW-420 type
 - A piezo type

The SW-420 sensors are digital sensors - they give either an On or Off signal.

The piezo sensors are ostensibly analog. This will come up later.

I also wanted to get notifications on my phone when a machine started, or stopped. I don't know mobile development, so I figured I'd go with text messages - the idea being that anyone in the house could sign up to get a text notification when a machine started or stopped. 

# Let's Get Started

## Phase 1:

I started by buying a pack of five SW-420 sensors from Amazon from a company called WINGONEER.

I was following this Instructables video: https://www.instructables.com/Vibration-Sensor-SW-420-Raspberry-Pi/

It turns out buying sensors on Amazon from all-caps companies that ship from China is not the best plan - none of the five sensors actually worked!

## Phase 2:

Here's where I admit to being a lazy noob. I don't want to do a bunch of wiring or electrical engineering - that's not the point of this project. I want to plug a sensor into a board and have it just work. 

Enter the Grove Pi board - https://wiki.seeedstudio.com/Grove-Starter-Kit-for-Raspberry-Pi-Pico/

This is a plug-and-play add-on board (a "shield") for a Raspberry Pi. Grove offers a number of 3-pin sensors that plug into the board and just work.

At this point in the project, I bought a Grove Pi and five Grove SW-420 vibration sensors. I also bought 5 Grove piezo vibration sensors, as I thought maybe I'd get better results with an analog sensor. 

#### Phase 2 Results:

These sensors work well, but they have some caveats. They have on-board potentiometers that control the sensitivity of the sensor, but there's no documentation on the settings of the potentiometer - where is "most sensitive" and where is "least sensitive"? Also, the potentiometers just keep turning - you can rotate them left or right forever, meaning there's no way to really know where the potentiometer is set just by looking at it. 

Additionally, the piezo vibration sensors, while supposedly being analog, are actually digital - they are read through an analog port, but only give low or high signals (~1000 or ~64000). There's no in-between, which is somewhat annoying. 

The piezo sensors are also somewhat difficult to use - they're non-intuitive, and it seems like the piezo film can be affected just by the wires that attach the film to the board. Meaning you might be getting signal just from the sensor sitting unconnected to anything. Also because they're a film, they react to disturbances of the film - if it's attached to a laundry machine, the vibrations of the machine may not actually translate to the film.

So at this point I switched back to using the SW-420s. 

## Phase 3:

Here I decided I also wanted to detect if a laundry machine door has been opened or closed. I originally thought I could detect this with the vibration sensor, but quickly realized they were a poor fit for this; they're sensitive, but it's tough to dial in the sensitivity to the point where they're sensitive enough to detect the door opening or closing on the machine they're attached to, but not so sensitive that they'd detect other vibrations around them.

So I decided to also use a Grove magnetic switch (a Reed switch) - https://wiki.seeedstudio.com/Grove-Magnetic_Switch/

I figured I could attach the magnetic switch to a laundry machine next to the door, and then attach a magnet to the door so that when it opened, the switch would read open, and then when the door was closed again, the magnet on the door would rest against the magnetic switch and close the circuit. Easy-peasy. 

## Phase 4:

Originally I had planned to have the single Raspberry Pi 3B+ board connected to both the laundry and dryer machines. However, the Grove sensors attach to the Grove board with a 3-pin cable that has a limited length - the maximum length is 50cm (20 inches). 20 inches is a relatively long distance, but not long enough to actually connect to both machines. Maybe it would be long-enough for the vibration sensors, but it wouldn't be long enough to connect to magnetic switches on each door.

#### Pivot!

Time to dramatically scale up the complexity of this project!

At this point I needed one Raspberry Pi board for each machine. Each individual board will be responsible for vibration and door sensors for one machine.

But I also needed a central system (or at least, I thought I did - I maybe could have built a system somehow that was decentralized, but ... I didn't). I still wanted text message notifications, which meant a single system to coordinate the interaction between sensor readings and text notifications. 

So here's what I decided:

I would attach a sensor deck to each machine. Each individual sensor apparatus would detect events (machine started, door opened, etc), and send those events to an event queue running on a central system. The central system would read events and, if a user has signed up for a notification for that type of event for that machine, would send out a notification to that user's phone. 

Here's the system design I came up with:
![[system-diagram.drawio 1.png]]

- I would use a Raspberry Pi Pico W as the board for each machine. 
	- The Pico W has a wi-fi chip, whereas the original Pico does not. For my application, I needed a network connection.
	- Grove sells a Grove Pi for the Pico that I could use to attach the Grove sensors to. 
	- The Pico W is much cheaper than a full Raspberry Pi 3B+/4/etc.
- Each Pico W would run a Python script that would read the sensors and turn the sensor signals into events.
- If an event is detected, the Pico W Python script would send an event to a RabbitMQ server running on the Raspberry Pi 3B+.
- A Java application, based on Spring, would also be running on the Raspberry Pi 3B+. 
	- This would subscribe to events from the RabbitMQ server
	- It would also allow users to sign up for notifications for a particular machine + event combination (e.g. "notify me the next time the washer door opens").
	- When an event was detected for which a user had subscribed to, this system would use Twilio to send a text message to the user's phone.
- The Spring application would persist data to an external database, e.g. MySQL.
- The Spring application would also expose APIs for things like adding a new user, getting a list of devices, and getting the most recent event for each device.
- A local-only website would access these data APIs to display the machines and their current / most recent states. The website would have input fields for users to sign up for notifications, as well as to manually submit events for each device. 

There were only a few problems with this system design:
1. Despite a 12 year career working in Java, I've never used Spring before.
2. I've never used RabbitMQ before.
3. My Python skills are minimal.
4. My database skills are minimal.
 
 (You might ask: what have I been doing with my career that I haven't used Spring or databases? Building custom pipelines for genetic data analysis and visualization is the answer!)

## Phase 5:

Did I mention that I'm lazy?

I _could_ learn Spring, and RabbitMQ, and Twilio integration, and database, and and and ...

Or ... 

I could have ChatGPT do things for me. 😅

First things first - initially I asked ChatGPT to generate a data model for the Java Spring application. I iterated on a number of prompts, but here's an example of one I used:

```
I am working on a project for my house. A vibration sensor will be attached to my washer and dryer. The sensor will detect when the machine is running and when it stops. If possible, the sensor will detect when the machine door is opened / closed. These sensors will connect to a raspberry PI device, which will connect to my home network and allow myself or other users to check the status of each machine. Ideally users will be able to receive notifications via text based on events like "machine finished running". 

Design the production-grade data model necessary for the software for this project.
```

I had a lot of success by telling ChatGPT to pretend it was a software engineer, e.g.:

```
Pretend you are a senior software engineer. Generate UML class outlines in text including variables and methods for this architecture.
```

I tweaked the data model descriptions it gave, and asked it to turn the descriptions into UML class outlines. Eventually I got ChatGPT to generate class outlines for the data model it created. It generated the outlines in text format like this:

```
+------------------+ 
| Event : class | 
+------------------+ 
| id: Long | 
| deviceId: long | 
| timestamp: LocalDateTime| 
| eventType: EventType| 
+------------------+ 
| + getId() : Long | 
| + getDeviceId() : Long | 
| + getTimestamp() : LocalDateTime | 
| + getEventType() : EventType | 
+---------------+

+------------------+ 
| EventType : enum | 
+------------------+ 
| DEVICE_STARTED | 
| DEVICE_STOPPED | 
| DEVICE_DOOR_OPENED| 
| DEVICE_DOOR_CLOSED| 
+------------------+
```

Then I fed the project description and class outlines into ChatGPT and asked it to generate Java code for the project:

```
Pretend you are a senior software engineer. Expand these class outlines into Java code using Spring Data JPA and Lombok annotations. Include required imports.
```

It gave me some basic code, which I then fed back into ChatGPT again. I did this process a number of times, tweaking or editing the code it gave me and feeding it back in, asking for improvements and additions:

```
Pretend you are a senior software engineer. Generate java code for additional java classes that would be required for this application. Additionally required classes include, but are not limited to: message queue-related classes for reading messages from rabbitMQ, text-notification and receiving classes using Twilio, and controller class(es) for determining, based on message queue messages, whether and which users should be notified.
```

This was one of my favorite parts of this project. Using ChatGPT to generate Spring code was super easy, but most of the code it produced was somehow incorrect. In order to fix the code it gave me, I had to learn enough Spring to know why it was wrong and how to fix it. This method of development allowed me to learn Spring really quickly, and also make much faster progress than I would have by starting from scratch.

Eventually I started asking ChatGPT to generate tests for the code I had. It was less successful at this process - it would use the Mockito framework to generate tests, but by doing so, wasn't actually testing the code. It would, for example, generate a test class that would mock the result from the method, then it would call that method and check that the (mocked) result was what it expected. This wasn't really useful, but it made me think about how to actually test the code, which also made me learn a lot more Spring.

Getting the tests working took a fair amount of time - figuring out what Spring annotations were needed to test database classes was the most time-consuming. 

Eventually, though, I was able to develop a mostly-complete test suite for the application. 

Then came the fun part of actually setting up the system!

## Phase 6:

Uh-oh, roadblocks ahead!

Setting up RabbitMQ was super easy. I followed the directions at these links, and was able to get RabbitMQ up and running really quickly:

http://pont.ist/rabbit-mq/

https://www.garybell.co.uk/setting-up-rabbimq-on-a-raspberry-pi/

It turns out, the Raspberry Pi 3B+ doesn't natively have a Java 17 JDK available. Fortunately for me, other people have run into this problem, and there is a company that develops a Java 17 JDK for the Raspberry Pi 3B+ - the BellSoft Liberica JDK.

https://forums.raspberrypi.com/viewtopic.php?t=328269

https://bell-sw.com/pages/downloads/

I ran into a bunch of other minor roadblocks trying to get everything working. The Maven version available from the Raspberry Pi 3B+ software repo (via apt-get) doesn't work with Java 17, so I had to download that manually. I ran into some issues with Spring application properties, and making sure they matched exactly with, e.g. the RabbitMQ queue name (turns out you don't use quotes around the property values in the Spring application.properties file!). 

There were other issues I ran into, but I don't remember what they all were, so I can't describe them here. I'm sorry for that - I wish I could describe all of the issues I ran into, so that I can help others avoid them! If you're trying to implement this system yourself and running into issues, feel free to reach out and I might be able to help. 

Eventually I got everything running and connected together. I could ping the APIs and get results, and I could submit events to the RabbitMQ system manually (using CURL) and see the Java system processing those events. I got Twilio integration working, and was able to get text messages to my smartphone based on the events submitted to the RabbitMQ server.

## Phase 7:

Next phase was developing the Python script for the Raspberry Pi Pico W's. 

Once again I turned to ChatGPT:

```
I have a Raspberry Pi Pico W, which is connected to a vibration sensor which is attached to a washing machine. The vibrations from the machine are intermittent and imprecise - the sensor is not guaranteed to pick up any single vibration. 

Pretend you are a senior software engineer with many years of experience developing IoT device software. 

What code would you write to detect, based on the vibration sensor, that the washing machine has started vs stopped. You might use a hidden markov model or other real-time statistical analysis techniques, but remember this must fit the memory profile of a raspberry pi pico w.
```

In this case, the first answer ChatGPT gave was the best. It generated Python code to compute the Root Mean Square of the sensor readings, and detect events based on that value. The RMS value is a measure of the average signal over the last N readings.

The biggest roadblock I ran into during this phase of development was figuring out how to get the Raspberry Pi Pico W to send events to the RabbitMQ server. The Raspberry Pi Pico W does not run a full version of Python (it uses MicroPython), and can't use the libraries that are commonly used for interacting with RabbitMQ, e.g. Pika. 

I tried to send events over the wire as byte arrays, but this didn't work out - I wasn't able to figure out the exact bytes required to send an AMQP event to RabbitMQ. 

Eventually I settled on using regular HTTP requests to post events to RabbitMQ. It's not optimal, but it works, which is the important part!

