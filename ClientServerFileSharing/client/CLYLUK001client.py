import os
import socket
import struct
import sys

# CLYLUK001 client

# Dictionary of command and key pairs
commands = {"q": "0", "u": "1", "d": "2", "l": "3"}


# Header encoding and decoding functions. Headers store length of messages
def encodeHeader(messagelength):
    messageheader = struct.pack("!I", messagelength)
    return messageheader


def decodeHeader(messageheader):
    messagelength = struct.unpack("!I", messageheader)[0]
    return messagelength


# Prompt user to enter the server host name or IP address to make a connection
hostIP = input("Please enter the server host name or IP address:\n--> ")
portNumber = int(input("Please enter the server port:\n--> "))

try:
    # create client socket
    clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  
except socket.error as e:
    print("Error: A socket could not be created. The program will now close.")
    sys.exit()

try:
    # Successful connection attempt to socket
    clientSocket.connect((hostIP, portNumber))
    print(f"Successfully connected to {hostIP}:{portNumber}\n")

except socket.error as e:
    # Unsuccessful connection attempt to socket
    print("Error: A connection to the server has been unsuccessful. The program will now close")
    sys.exit()

# Interface for client
choice = ""
print("Greetings! Welcome to the File Transfer Protocol! \n"
      "Commands:\n"
      "q - Exit program \n"
      "u - Upload file to the server \n"
      "d - Download file from the server \n"
      "l - List available files on server \n"
      )
while choice != "q":
    choice = input("Please enter a command: \n--> ").lower().strip()

    if choice == "q":
        header = encodeHeader(1)  # Send '0' as exit command
        clientSocket.sendall(header + commands["q"].encode())

    elif choice == "u":

        fileExists = False

        try:
            filePath = r"" + input(
                "Please enter file path of the file you would like to upload:\n--> ").strip()

            # Filename will be extracted from path
            fileName = os.path.basename(filePath)
            fileSize = str(os.path.getsize(filePath))
            fileExists = True

        except FileNotFoundError:
            print("Error: The file that you are trying to upload cannot be found.\n")

        if fileExists:
            # Send command '1'
            header = encodeHeader(1)
            clientSocket.sendall(header + commands["u"].encode())

            # Send filename to server
            # noinspection PyUnboundLocalVariable
            header = encodeHeader(len(fileName))
            clientSocket.sendall(header + fileName.encode())

            # Receive boolean indicating whether file already exist on server
            value = clientSocket.recv(1)
            fileOnServer = struct.unpack("!?", value)[0]

            # If file does not exist on the server
            if not fileOnServer:
                key = input(
                    # Prompts user if they want a key for their file
                    "Please enter a file key (or press enter to skip):\n--> ")

                if len(key) == 0:  # This makes key empty if user doesn't want a key
                    key = " "

                # noinspection PyUnboundLocalVariable
                with open(filePath, 'rb') as f:
                    fileContent = f.read()

                header = encodeHeader(len(key))
                # Key is sent to the server
                clientSocket.sendall(header + key.encode())

                # noinspection PyUnboundLocalVariable
                header = encodeHeader(len(fileSize))
                # File size is sent to the server
                clientSocket.sendall(header + fileSize.encode())

                header = encodeHeader(len(fileContent))
                # File content as bytes is sent to the server
                clientSocket.sendall(header + fileContent)

                print("Successfully uploaded file to server.\n")
            else:
                # The file already exist on the server so do not upload
                print(f"{fileName} already exists on server\n")

    elif choice == "d":  # Download a file from the server

        # send command '2'
        header = encodeHeader(1)
        clientSocket.sendall(header + commands["d"].encode())

        fileName = input("Please enter the name of file which you would like to download:\n--> ")

        # Send requested filename to server
        header = encodeHeader(len(fileName))
        clientSocket.sendall(header + fileName.encode())

        # receive boolean
        value = clientSocket.recv(1)
        fileExists = struct.unpack("!?", value)[0]

        # if file doesn't exist on server db, nothing to download
        if not fileExists:
            print("Error: The file that you are trying to download cannot be found\n")

        else:
            # Checks if file is private or public
            value = clientSocket.recv(1)
            isPublic = struct.unpack("!?", value)[0]

            if isPublic:

                # receive fileContent
                header = clientSocket.recv(4)
                length = decodeHeader(header)
                fileContent = clientSocket.recv(length)

                with open(fileName, "wb") as f:
                    f.write(fileContent)

                print(f"You have successfully downloaded {fileName}.\n")

            else:
                key = input("File that you have requested is protected. Please enter the key:\n> ")

                # send key
                header = encodeHeader(len(key))
                clientSocket.sendall(header + key.encode())

                # boolean received whether correct key is given
                value = clientSocket.recv(1)
                isCorrect = struct.unpack("!?", value)[0]

                if isCorrect:

                    header = clientSocket.recv(4)
                    length = decodeHeader(header)
                    fileContent = clientSocket.recv(length)

                    with open(fileName, "wb") as f:
                        f.write(fileContent)

                    print(f"Successfully downloaded {fileName}.\n")

                else:
                    print("The incorrect file key has been provided.\n")

    elif choice == "l":
        header = encodeHeader(1)
        clientSocket.sendall(header + commands["l"].encode())

        header = clientSocket.recv(4)
        length = decodeHeader(header)
        filelist = clientSocket.recv(length).decode()
        print(filelist + "\n")

clientSocket.close()
