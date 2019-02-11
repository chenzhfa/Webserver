CREATE TABLE Locations(
    idLocation INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE Devices(
    idDevice INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, 
    macAddress CHAR(17) NOT NULL,
    kLocation INT UNSIGNED,
    FOREIGN KEY (kLocation) REFERENCES Locations (idLocation)
);

CREATE TABLE Readings(
    idReading INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL,
    kDevice INT UNSIGNED,
    kLocation INT UNSIGNED,
    FOREIGN KEY (kDevice) REFERENCES Devices (idDevice),
    FOREIGN KEY (kLocation) REFERENCES Locations (idLocation)
);

CREATE TABLE R_Values (
    idValue INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sensorProgressive SMALLINT UNSIGNED NOT NULL,
    reading REAL (4,1) NOT NULL,
    kReading INT UNSIGNED NOT NULL,
    FOREIGN KEY (kReading) REFERENCES Readings (idReading)
);

/*Sign a new location*/
INSERT INTO Locations(name) 
VALUES("LocationProva");

/*Sign a new device*/
INSERT INTO Devices(macAddress, kLocation) 
VALUES("ff:gg:hh:ii:ll:mm", 2);

/*Sign a new reading*/
INSERT INTO Readings(data, kDevice,kLocation) 
VALUES("1990-09-01", 2, 2);

INSERT INTO R_Values(sensorProgressive, reading, kReading) 
VALUES (1, 10.4, 1); /**/

/*Get location of a Device*/
SELECT kLocation 
FROM Devices 
INNER JOIN Locations ON kLocation = idLocation 
WHERE idDevice = 1 /*Change this parameter*/

/*Get ID for a Device*/
SELECT idDevice
FROM Devices
WHERE macAddress = "ff:gg:hh:ii:ll:mm";

/*Get all Readings of a Device*/
SELECT macAddress, data, name, sensorProgressive, reading
FROM Devices 
INNER JOIN Readings ON idDevice = kDevice
INNER JOIN Locations ON Readings.kLocation = idLocation
INNER JOIN R_Values ON idReading = kReading  
WHERE idDevice = 2
GROUP BY idReading, sensorProgressive, reading;