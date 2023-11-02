# Makefile
# Brian Mbawa
# 09 April 2023

JAVAC=/usr/bin/javac

.SUFFIXES: .java .class

SRCDIR=src

BINDIR=bin


$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) -sourcepath $(SRCDIR) $<


CLASSES=PVector.class \
	HorizontalPanel.class SettingsPanel.class \
	Obstacle.class \ Animal.class \
	Frame.class \
	SimulationPanel.class SimJFrame.class \
	Terrain.class TerrainLoader.class TerrainChooser.class \
	Simulation.class \

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/*.class
	
run: $(CLASS_FILES)
	java -cp bin Simulation
