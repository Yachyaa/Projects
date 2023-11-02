
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
JAVAC=/usr/bin/javac
JAVA=/usr/bin/java

$(BINDIR)/%.class: $(SRCDIR)/%.java	
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<
	
CLASSES2= HungryMover.class GamePanel.class FallingWord.class CatchWord.class HungryWord.class Score.class ScoreUpdater.class, TypingTutorApp.class WordDictionary.class WordMover.class

CLASSES=$(CLASSES2:%.class=$(BINDIR)/%.class)
default: $(CLASSES)

runTypingTutorApp: $(CLASSES)
	$(JAVA) -cp $(BINDIR) TypingTutorApp

	