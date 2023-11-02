import socket
import struct
import sqlite3  # Library allowing sql database to be created
import hashlib  # Library to hash file keys which will be stored in database.
import threading  # Library to handle threading

# random string used when hashing fileKeys.
salt = b'the quick brown fox jumps over the lazy dog'

IP = socket.gethostbyname(socket.gethostname())
port = 12004

print("Server started on {}:{}".format(IP, port))

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serverSocket.bind(('', port))
serverSocket.listen(1)

userRequest = ""


dbClosed = False


def headerEnc(length):
    header = struct.pack("!I", length)
    return header


def headerDec(header):
    length = struct.unpack("!I", header)[0]
    return length

#  method that handles clients that connect to server concurrently
def handleClient(connectionSocket, IP, port, connection):
    print(f"New client has connected with the IP address {IP} using port {port}")

    connected = True
    while connected:

        # Receive user input
        print("Receiving user input from client")

        header = connectionSocket.recv(4)
        length = headerDec(header)
        userRequest = connectionSocket.recv(length).decode()
        print("User is requesting: " + userRequest)

        if userRequest == "0":  # quit
            print("User has chosen to quit")
            connectionSocket.close()
            connection.close()  # end database connection
            dbClosed = True
            isConnected = False
            connected = False

        elif userRequest == "1":  # upload
            cursor = connection.cursor()
            fileExists = False

            # receive fileName
            header = connectionSocket.recv(4)
            length = headerDec(header)
            fileName = connectionSocket.recv(length).decode()

            # Check if file exists on database
            cursor.execute("SELECT * FROM files WHERE fileName = ?", (fileName,))
            result = cursor.fetchone()

            if result != None:
                fileExists = True
                # send boolean
                value = struct.pack("!?", fileExists)
                connectionSocket.send(value)

            if not fileExists:

                fileExists = False
                # send boolean
                value = struct.pack("!?", fileExists)
                connectionSocket.send(value)

                # receive key
                header = connectionSocket.recv(4)
                length = headerDec(header)
                key = connectionSocket.recv(length).decode()
                # print(f"Filekey received was {key}")

                # receive fileSize
                header = connectionSocket.recv(4)
                length = headerDec(header)
                fileSize = connectionSocket.recv(length).decode()
                # print(f"FileSize received was {fileSize}")

                # receive fileContent
                header = connectionSocket.recv(4)
                length = headerDec(header)
                fileContent = connectionSocket.recv(length)
                # print(f"FileContent received was {fileContent}")

                if key != " ":  # if string is not blank, hash key using the sha256 algorith before storing in database
                    key = hashlib.sha256(key.encode() + salt).hexdigest()

                connection.execute('INSERT INTO files (fileName, fileContent, fileKey, fileSize) VALUES (?, ?, ?, ?)',
                                   (fileName, sqlite3.Binary(fileContent), key, fileSize + " Bytes"))
                connection.commit()  # save changes to database
                # print("Successfully added {} to database".format(fileName))

        elif userRequest == "2":  # download
            cursor = connection.cursor()

            # receive FileName
            header = connectionSocket.recv(4)
            length = headerDec(header)
            fileName = connectionSocket.recv(length).decode()
            # print(f"FileName received was {fileName}")

            # Check if file exists in database
            cursor.execute("SELECT fileKey FROM files WHERE fileName = ?", (fileName,))
            result = cursor.fetchone()

            if result == None:
                fileExists = False

                # send boolean
                value = struct.pack("!?", fileExists)
                connectionSocket.send(value)

            else:
                fileExists = True

                # send boolean
                value = struct.pack("!?", fileExists)
                connectionSocket.send(value)

                fileKey = result[0]

                cursor.execute("SELECT fileContent FROM files WHERE fileName=?", (fileName,))
                fileContent = cursor.fetchone()[0]

                if fileKey == " ":
                    isPublic = True

                    # send boolean
                    value = struct.pack("!?", isPublic)
                    connectionSocket.send(value)

                    # send fileContent
                    header = headerEnc(len(fileContent))
                    connectionSocket.sendall(header + fileContent)

                else:
                    isPublic = False

                    # send boolean
                    value = struct.pack("!?", isPublic)
                    connectionSocket.send(value)

                    # receive userKey
                    header = connectionSocket.recv(4)
                    length = headerDec(header)
                    userKey = connectionSocket.recv(length).decode()

                    # print("User key:" , userKey)

                    hashedUserKey = hashlib.sha256(userKey.encode() + salt).hexdigest()

                    # print("hashedUserKey", hashedUserKey)
                    # print("fileKey" , fileKey)

                    if hashedUserKey == fileKey:
                        isCorrect = True

                        # send boolean
                        value = struct.pack("!?", isCorrect)
                        connectionSocket.send(value)

                        # send Filecontent
                        header = headerEnc(len(fileContent))
                        connectionSocket.sendall(header + fileContent)

                    else:
                        isCorrect = False

                        # send boolean
                        value = struct.pack("!?", isCorrect)
                        connectionSocket.send(value)

        elif userRequest == "3":  # list
            # Create a cursor object
            cursor = connection.cursor()

            # Execute a SELECT query to retrieve all names in the "names" table
            cursor.execute('SELECT fileName, fileSize, fileKey FROM files')

            # Fetch all the results and store them in a list
            results = cursor.fetchall()
            list = ""

            if len(results) != 0:
                list += "List of files on server:"
                for result in results:
                    if result[2] != " ":
                        list += "\n" + "[private]\t" + result[0] + "\t" + f"[{result[1]}]"
                    else:
                        list += "\n" + "[public]\t" + result[0] + "\t" + f"[{result[1]}]"
            else:
                list += "There are no files on the server."

            # print(list)
            header = headerEnc(len(list))
            connectionSocket.sendall(header + list.encode())

    connectionSocket.close()



while True:
    # Connect to database
    connection = sqlite3.connect('fileDatabase.db', check_same_thread=False)
    connection.execute(
        'CREATE TABLE IF NOT EXISTS files (id INTEGER PRIMARY KEY, fileName TEXT, fileContent BLOB, fileKey TEXT, fileSize TEXT)')  # blob datatype used for storing files like videos, txt etc
    # cursor object lets us scan through a database and make queries
    dbClosed = False

    connectionSocket, address = serverSocket.accept()  # accept connections

    # creates threads
    thread = threading.Thread(target=handleClient, args=(connectionSocket, IP, port, connection))  #creates thread using the method in "target"
    thread.start()

    print(f"Clinets connected: {threading.active_count() - 1}")  # -1 bc the main() thread counts




