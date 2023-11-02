import random
import sys


def main():
    pages = ''.join(str(random.randint(0, 9)) for _ in range(random.randint(1, 20)))


    # ...TODO...
    size = int(sys.argv[1])
    print("FIFO", FIFO(size,pages), "page faults")
    print("LRU", LRU(size,pages), "page faults")
    print("OPT", OPT(size,pages), "page faults")


    # size = random.randint(1, 10)
    # frameWindow = random.randint(1, 7)
    #
    # print("FIFO")
    # fifofaults = FIFO(frameWindow, refString)
    # print("reference string:", refString, "\nwindow size :", frameWindow, "\npage faults:", fifofaults, "\n")
    #
    # print("LRU")
    # lruFaults = LRU(frameWindow, refString)
    # print("reference string:", refString, "\nwindow size :", frameWindow, "\npage faults:", lruFaults, "\n")
    #
    # print("OPT")
    # optFaults = OPT(frameWindow, refString)
    # print("reference string:", refString, "\nwindow size :", frameWindow, "\npage faults:", optFaults, "\n")


def FIFO(frameSize, refString):
    referencedFrames = []  # pages that are in main memory
    pageFaults = 0

    for address in refString:  # iterate through reference string
        if address not in referencedFrames:  # if referenced page has not in memory
            # print("address", address, "NOT in memory")
            if len(referencedFrames) < frameSize:  # add elements when list is empty
                referencedFrames.append(address)
                pageFaults += 1
            else:
                referencedFrames.pop(0)  # remove oldest item in referenced frame (first frame to be added)
                referencedFrames.append(address)
                pageFaults += 1

    return pageFaults


def LRU(frameSize, refString):
    memory = []  # pages that are in main memory
    pageFaults = 0

    for address in refString:
        if address not in memory:
            if len(memory) < frameSize:
                memory.append(address)
                pageFaults += 1

            else:
                memory.pop(0)
                memory.append(address)
                pageFaults += 1

        else:
            addressToMove = memory.index(address)
            memory.pop(addressToMove)
            memory.append(address)

    return pageFaults


def OPT(frameSize, refString):
    stringRefString = str(refString)
    memory = []  # pages that are in main memory
    pageFaults = 0

    for stringAddress in str(refString):

        if stringAddress not in memory:
            if len(memory) < frameSize:
                memory.append(stringAddress)
                stringRefString = removeFirstNumber(stringRefString)
                pageFaults += 1
            else:
                replaceAddress = whatToReplace(memory, stringRefString)
                if replaceAddress == "":
                    memory.pop(0)
                    memory.append(stringAddress)

                else:
                    findIndex = memory.index(replaceAddress)
                    memory.pop(findIndex)
                    memory.append(stringAddress)
                    stringRefString = removeFirstNumber(stringRefString)
                pageFaults += 1

        else:
            stringRefString = removeFirstNumber(stringRefString)

    return pageFaults


# Returns the shortest time the next adderess is referenced
def timeUntilNextReference(findAddress, referenceString):
    time = 0;
    found = False

    for address in str(referenceString):
        time += 1
        if int(findAddress) == int(address):
            found = True
            break
    if not found:
        return -1

    return time


# Returns what address can be replaced in memory
def whatToReplace(memory, referenceString):
    # print("refString used in replace is: ", referenceString)
    currentReplace = ""
    timeOfCurrentReplace = -1

    for memoryAddress in memory:
        nextReference = timeUntilNextReference(memoryAddress, referenceString)
        if nextReference == -1:
            return memoryAddress
        if nextReference > timeOfCurrentReplace:
            timeOfCurrentReplace = nextReference
            currentReplace = memoryAddress

    return currentReplace


def removeFirstNumber(my_number):
    if len(str(my_number)) > 1:
        my_number_str = str(my_number)
        new_number = my_number_str[1:]
    else:
        new_number = my_number

    return new_number


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print ("Usage: python paging.py[number of page frames]")
    else:
        main()
