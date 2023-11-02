#ORYFAB001 client implementation

import hashlib
import struct
import sqlite3
import sys
import socket
import os

#dictionary containing command key value pairs
commands = {"exit": "0" , "upload": "1", "download": "2", "list": "3"}

#Header encoding and decoding functions. Headers store length of messages
def headerEnc(length):
    header = struct.pack("!I", length)
    return header

def headerDec(header):
    length = struct.unpack("!I", header)[0]
    return length

#connect to server with inputted hostName and port
hostName = input("Enter server host name or IP address:\n> ")
port = int(input("Enter server port:\n> "))

try:
    clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    # create client socket
except socket.error as e:
    print("[Error] Could not create socket:")
    sys.exit()

try:
    clientSocket.connect((hostName, port))  #attempt to connect to server
    print(f"Successfully connected to {hostName}:{port}\n")

except socket.error as e:
    print("[Error] Could not connect to server:")
    sys.exit()

#Interface for client
command = ""
print("Welcome to the file transfer protocol. \n"
    "Commands:\n"
    "exit - terminate connection \n"
    "upload - upload a public or password protected file to the server \n"
    "download - download a file from the server \n"
    "list - print a list of server files \n"
)
while command != "exit":
    command = input("Enter a command: \n> ").lower().strip()


    if command == "exit":
        header = headerEnc(1)   # send exit command to server as "0"
        clientSocket.sendall(header + commands["exit"].encode())

    elif command == "upload":

        fileExists = False

        try:
            filePath = r"" + input("Enter file path:\n> ").strip()  # r lets us store file path as a raw literal without the need for escape characters for special characters
            fileName = os.path.basename(filePath)  # extract file name from path
            fileSize = str(os.path.getsize(filePath))
            fileExists = True

        except FileNotFoundError:
            print("File could not be found.\n")

        if fileExists:
            #send upload command as "1" to server only if file is found on client
            header = headerEnc(1)
            clientSocket.sendall(header + commands["upload"].encode())

            #send file name to server
            header = headerEnc(len(fileName))
            clientSocket.sendall(header + fileName.encode())

            #receive boolean indicating whether file already exist on server db
            value = clientSocket.recv(1)
            fileOnServer = struct.unpack("!?", value)[0]

            # if file doesn't exist on server db upload
            if not fileOnServer:
                key = input("Set file key (or press enter to skip):\n> ")  # asks user to optionally provide password for file access

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

                print("Successfully uploaded file to server.\n")
            else:
                print(f"{fileName} already exists on server\n") # file exists on database, don't upload.

    elif command == "download":  # download file

        #send download command to server as "2"
        header = headerEnc(1)
        clientSocket.sendall(header + commands["download"].encode())

        fileName = input("Enter name of file:\n> ")

        #Send requested filename to server
        header = headerEnc(len(fileName))
        clientSocket.sendall(header + fileName.encode())

        #receive boolean
        value = clientSocket.recv(1)
        fileExists = struct.unpack("!?", value)[0]

        # if file doesn't exist on server db, nothing to download
        if not fileExists:
            print("[Error] File not found\n")

        else:
            #check if is public or private
            value = clientSocket.recv(1)
            isPublic = struct.unpack("!?", value)[0]

            if isPublic:

                #receive fileContent
                header = clientSocket.recv(4)
                length = headerDec(header)
                fileContent = clientSocket.recv(length)

                with open(fileName, "wb") as f:
                    f.write(fileContent)

                print(f"Successfully downloaded {fileName}.\n")

            else:
                key = input("File is protected. Please provide the key:\n> ")

                #send key
                header = headerEnc(len(key))
                clientSocket.sendall(header + key.encode())

                #receive boolean wheather correct key entered
                value = clientSocket.recv(1)
                isCorrect = struct.unpack("!?", value)[0]

                if isCorrect:
                    #receive file content
                    header = clientSocket.recv(4)
                    length = headerDec(header)
                    fileContent = clientSocket.recv(length)

                    with open(fileName, "wb") as f:
                        f.write(fileContent)

                    print(f"Successfully downloaded {fileName}.\n")

                else:
                    print("Incorrect file key provided.\n")

    elif command == "list":
        header = headerEnc(1) #send list command to server as "3"
        clientSocket.sendall(header + commands["list"].encode())

        header = clientSocket.recv(4)
        length = headerDec(header)
        list = clientSocket.recv(length).decode()
        print(list + "\n")

clientSocket.close()
