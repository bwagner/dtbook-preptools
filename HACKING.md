This HACKING file describes the development environment.

Copyright (C) 2012 Swiss Library for the Blind, Visually Impaired
and Print Disabled

Copying and distribution of this file, with or without modification,
are permitted in any medium without royalty provided the copyright
notice and this notice are preserved.

Overview
==========

The prep tool is used as an aid to enrich
[DTBook XML](http://en.wikipedia.org/wiki/DTBook) with markup needed
to render Braille. It is implemented as a plugin to the oXygen XML
editor.

Why edit the text as opposed to the document tree
-------------------------------------------------

The current implementation of the plugin does the markup enrichment
directly in the text and does not parse the XML. This has some
problems in that we have to deal with gruesome regular expressions
which try to match across the XML tags (see the whole business with
the skipper and the interval tree, trying to hide the XML tags). So on
the one hand this is a drawback. On the other hand it keeps us from
having to synchronize the document tree with the text, as while we are
manipulating the document tree the user might at the same time modify
the text.

Class Overview
==============

PrepToolsPluginExtension.java
-----------------------------

Deals with initializing the (Swing) Menu bars, tool bar, etc. It also
makes sure that some menu items which are implicitly resetting the
state of a document are wrapped, so that the prep tool can be notified
of these events. These are the "Revert" and the "Save as"
menus. Similarly when switching to another prep tool while the current
one is still active we need to make sure that the user is warned and
the state of the prep tool is reset.

OxygenEditGrouper.java
----------------------

Group a bunch of edits so that they can be undone with one undo

DocumentMetaInfo.java
---------------------

Store info such as current active prep tool, state of each prep tool
or current cursor position. This is needed to reflect it in the tool
bar but also for example to check if the user changed the cursor
position behind the prep tools back.

PrepToolsValidatorPluginExtension.java
--------------------------------------

Not needed in favor of the MenuPlugger

AbstractPrepToolAction.java
---------------------------

PrepTool.java
-------------

PrepToolLoader.java
-------------------

TrafficLight.java
-----------------

VFormActionHelper.java
----------------------

Future work
===========

There is an effort underway within the
[DAISY pipeline 2 project](http://www.daisy.org/pipeline2) to create a
set of what they call pre-processing steps which have a similar goal:
To enrich XML with additional markup to make it ready for Braille
generation. These steps will probably be based on XML technologies,
i.e. XSLT and XProc and will also interact with the user, e.g. to
confirm an enrichment. How this interaction will be and if it could be
integrated in an oXygen editor plugin remains to be seen.