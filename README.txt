# JSketch (Android)

A very simple drawing application for Android devices. Allows users to draw rectangles, lines, circles of varying thicknesses and colours. Also supports moving shapes around, and saving the canvas and sharing it. 

This was my submission University of Waterloo's CS349 (User Interfaces) assignment 3. 

## Screenshots

![screenshot](screenshots/curio1.png)
![screenshot](screenshots/curio2.png)
![screenshot](screenshots/curio3.png)

## Running 

Import the project into Android Studio and deploy to a device.

## Notes

I chose to implement the "Export/Save" enhancement
If you tap the share button (at the bottom/end of the toolbar), you can share an image of the canvas via Android's
native share menu.

***NOTE*** This enhancement is better demonstrated on a device with lots of applications installed that accept 
the ACTION_SEND intent. By default, an AVD does not have ANY such supported applications (except the messaging 
application). In my experience, after clicking the share button on an AVD, the canvas will be saved, and a 
"choose conversation" dialog will appear (most likely becuase the only supported ACTION_SEND application installed 
is the messaging app - which has no existing conversations by default). However, after clicking the button, the 
canvas will be saved regardless of the user's selection as a PNG file on the device. This is visible if you go 
to the gallery app on the device, and you will be able to find the saved file there (this works on an AVD as well). 

In addition to the previous enhancement, I also implemented the ability to pan the canvas around with two fingers - this makes it easier to paint on the canvas in both orientations. Note: I did NOT implement pinch to zoom. I only implemented this enhancement to better support both landscape and portrait orientations (so you can view the entire canvas area in both orientations).  

The drawable area of the canvas is the white area, the grey area is outside of the canvas and cannot be drawn on.

The canvas area size is always 700dp x 700dp. In both orientations, you can pan the canvas around by dragging with two fingers to view areas of the canvas that are off screen.

The square and circle tools have minimum draw sizes. The user must drag far enough before the shape is drawn, otherwise it would 
get drawn in a size that is too small to select.

The line tool has a slightly larger "touch box" (compared to A2) to ensure that the user can easily select the line tool
with their finger. 

This app should work properly on any screen size, I designed it with this in mind. 

This was developed on MAC OSX EL CAPITAN with Android Studio 2.1.2

##ICONS

The icons used for the tools are the material design icons produced by google. See https://design.google.com/icons/ 
for the icons. The icons are licenced under Creative Commons Attribution 4.0 International License (CC-BY 4.0) 
(http://creativecommons.org/licenses/by/4.0/)