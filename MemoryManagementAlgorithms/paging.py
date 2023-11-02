# Yachyaa Toefy
# TFYYAC001
# Assignment 1

import random
import sys


def FIFO(frameSize, refString):
    referencedFrames = []  # pages that are in main memory
    pageFaults = 0

    for address in refString:  # iterate through reference string
        if address not in referencedFrames:  # if referenced page has not in memory
            pageFaults += 1
            if len(referencedFrames) < frameSize:  # add elements when list is empty
                referencedFrames.append(address)
            else:
                referencedFrames.pop(0)  # remove oldest item in referenced frame (first frame to be added)
                referencedFrames.append(address)

    return pageFaults


def LRU(frameSize, refString):
    memory = []  # pages that are in main memory
    pageFaults = 0

    for address in refString:
        if address not in memory:
            pageFaults += 1
            if len(memory) < frameSize:
                memory.append(address)
            else:
                memory.pop(0)
                memory.append(address)
        else:
            addressToMove = memory.index(address)
            memory.pop(addressToMove)
            memory.append(address)

    return pageFaults


def OPT(frameSize, refString):
    stringRefString = str(refString)
    memory = []  # pages that are in main memory
    pageFaults = 0

    for stringAddress in str(refString):  # iterate through reference string
        if stringAddress not in memory:  # address is not in memory
            pageFaults += 1
            if len(memory) < frameSize:  # memory not yet full
                memory.append(stringAddress)
                stringRefString = removeFirstNumber(stringRefString)  # remove first element of the reference string
            else:
                replaceAddress = whatToReplace(memory,
                                               stringRefString)  # find which element in memory is getting replaced
                if replaceAddress == "":  # if no element in memory is referenced again
                    memory.pop(0)
                    memory.append(stringAddress)

                else:
                    memory.pop(memory.index(replaceAddress))  # remove element from memory
                    memory.append(stringAddress)
                    stringRefString = removeFirstNumber(stringRefString)  # remove start element of reference string

        else:  # referenced address is in memory
            stringRefString = removeFirstNumber(stringRefString)
    return pageFaults


# Determines the amount of indexes until findAddress is referenced again in reference string
def timeUntilNextReference(findAddress, referenceString):
    time = 0;
    found = False

    for address in str(referenceString):
        time += 1
        if int(findAddress) == int(address):  # find address is referenced again in reference string
            found = True
            break
    if not found:   # find address not referenced again in reference string
        return -1

    return time


# Returns address that can be replaced in memory by finding
# the memory address with the longest time  until its referenced again
def whatToReplace(memory, referenceString):
    currentReplace = ""
    timeOfCurrentReplace = -1

    for memoryAddress in memory:
        # determines the amount of indexes until the memory address is referenced again in reference string
        timeNextReference = timeUntilNextReference(memoryAddress, referenceString)

        if timeNextReference == -1:  # memory address not referenced again therefore it can be replaced
            return memoryAddress

        if timeNextReference > timeOfCurrentReplace:  # time to reference current address > than current longest address
            timeOfCurrentReplace = timeNextReference
            currentReplace = memoryAddress

    return currentReplace


def removeFirstNumber(my_number):
    if len(str(my_number)) > 1:
        my_number_str = str(my_number)
        new_number = my_number_str[1:]
    else:
        new_number = my_number

    return new_number


def main():
    pages = ''.join(str(random.randint(0, 9)) for _ in range(random.randint(1, 20)))
    print("Reference string: ", pages)

    size = int(sys.argv[1])
    print("FIFO", FIFO(size, pages), "page faults")
    print("LRU", LRU(size, pages), "page faults")
    print("OPT", OPT(size, pages), "page faults")


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python paging.py[number of page frames]")
    else:
        main()
