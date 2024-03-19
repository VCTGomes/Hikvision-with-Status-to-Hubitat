This app and driver have the ability to import the locker, available on certain Hikvision intercoms, to Hubitat. The Lock button won't lock, since doorbell is only capable of emitting a pulse to unlock.

It uses ISAPi some HTML POST to unlock your door. I tested it on DS-KV6113-WPE1(B), but ISAPi is avaliable on varius Hikvision doorbells, alarms and more.

With the app, you're able of synchronizing the closed/open status based on a contact sensor. If you're not interested in having the status, it's possible to install [the light version](https://github.com/VCTGomes/Hikvision-Door-Control-to-Hubitat) that only unlocks the door.

This app and driver are based on the work of [matthewpetro](https://github.com/matthewpetro/hubitat-projects/tree/main/garage-door), who maintain the garage-door-app, which I modified to meet the needs of Hikvision and import the door status.

This is an initial version that I made for my own use and may improve in the future.

## Installation

### Install the driver.
1. On Hubitat, open App Code > + New app > Import and past the following URL: https://raw.githubusercontent.com/VCTGomes/Hikvision-with-Status-to-Hubitat/main/hik-door-app.groovy
1. Now we need the driver, which similar. Go to Drive Code > + New drive > Import > past the following URL and save it
https://raw.githubusercontent.com/VCTGomes/Hikvision-with-Status-to-Hubitat/main/hik-locker.groovy

### Set up the locker
1. Go to Apps page > +Add user app > press on Hikvision with Status
1. Type the IP from you intercom, its password and select a contact sensor to deliver close/open status.
1. On the devices page, there should be a device named "Hikvision Locker."
