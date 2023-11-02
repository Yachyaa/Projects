import hashlib
import os
import socket
import struct
import sqlite3


IP = input("Enter Server IP Address: \n")
port = int(input("Enter Server Port Number: \n"))

# IP = socket.gethostbyname(socket.gethostname())
# port = 12005

clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # create tcp socket
clientSocket.connect((IP, port))  # connect to server

userCommand = ""

def headerEnc(length):
    header = struct.pack("!I", length)
    return header

def headerDec(header):
    length = struct.unpack("!I", header)[0]
    return length

while userCommand != "0":
    userCommand = input("Enter Command [0-3]: \n"
                        "0 - quit \n"
                        "1 - upload \n"
                        "2 - download \n"
                        "3 - list \n")

    if userCommand == "0":
        header = headerEnc(len(userCommand))
        clientSocket.sendall(header + userCommand.encode())

    if userCommand == "1":

        fileExists = False

        try:
            filePath = r"" + input("Enter file path:\n").strip()  # r lets us store file path as a raw literal without the need for escape characters for special characters
            fileName = os.path.basename(filePath)  # extract file name from path
            fileSize = str(os.path.getsize(filePath))
            fileExists = True

        except FileNotFoundError:
            print("File not found.\n")

        if fileExists:

            #send command
            header = headerEnc(len(userCommand))
            clientSocket.sendall(header + userCommand.encode())

            #send file name
            header = headerEnc(len(fileName))
            clientSocket.sendall(header + fileName.encode())

            #receive boolean
            value = clientSocket.recv(1)
            fileOnServer = struct.unpack("!?", value)[0]

            if not fileOnServer:
                key = input("Set file key (or press enter to skip):\n")  # asks user to optionally provide password for file access

                if len(key) == 0:  # check if string is empty i.e user doesn't want to set a password
                    key = " "

                with open(filePath,'rb') as f:  # rb reads file as bytes and r is only for text files. Better since we don't know if it's text or a picture/ video etc
                    fileContent = f.read()

                header = headerEnc(len(key))
                clientSocket.sendall(header + key.encode())  # send key to server

                header = headerEnc(len(fileSize))
                clientSocket.sendall(header + fileSize.encode()) #send fileSize

                header = headerEnc(len(fileContent))
                clientSocket.sendall(header + fileContent)  # send file content as bytes to server

                print("------ UPLOADING FILE ------")
                print("Successfully uploaded file to server.\n")
            else:
                print("File already exists on server.\n")

    elif userCommand == "2":  # download file

        header = headerEnc(len(userCommand))
        clientSocket.sendall(header + userCommand.encode())

        fileName = input("Enter name of file:\n")

        #Send requested filename to server
        header = headerEnc(len(fileName))
        clientSocket.sendall(header + fileName.encode())

        #receive boolean
        value = clientSocket.recv(1)
        fileExists = struct.unpack("!?", value)[0]

        if not fileExists:
            print("File not found on database\n")

        else:
            #receive boolean
            value = clientSocket.recv(1)
            isPublic = struct.unpack("!?", value)[0]

            if isPublic:

                #receive fileContent

                header = clientSocket.recv(4)
                length = headerDec(header)
                fileContent = clientSocket.recv(length)

                print("------ DOWNLOADING FILE -------")
                with open(fileName, "wb") as f:
                    f.write(fileContent)

                print("File downloaded successfully.\n")

            else:
                key = input("File is protected. Please provide the key:\n")

                if key == "":
                    key = " "

                #send key
                header = headerEnc(len(key))
                clientSocket.sendall(header + key.encode())

                #receive boolean
                value = clientSocket.recv(1)
                isCorrect = struct.unpack("!?", value)[0]

                if isCorrect:
                    #receive file content
                    header = clientSocket.recv(4)
                    length = headerDec(header)
                    fileContent = clientSocket.recv(length)

                    with open(fileName, "wb") as f:
                        f.write(fileContent)

                    print("File downloaded successfully.\n")

                else:
                    print("Unable to retrieve file. Incorrect key provided.\n")

    elif userCommand == "3":
        header = headerEnc(len(userCommand))
        clientSocket.sendall(header + userCommand.encode())

        header = clientSocket.recv(4)
        length = headerDec(header)
        list = clientSocket.recv(length).decode()
        print(list + "\n")


print("\nProgram has ended.")
clientSocket.close()
