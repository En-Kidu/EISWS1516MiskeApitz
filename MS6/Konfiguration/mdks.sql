-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 17. Jan 2016 um 21:29
-- Server-Version: 10.1.9-MariaDB
-- PHP-Version: 5.6.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `mdks`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `medikament`
--

CREATE TABLE `medikament` (
  `medikament_id` int(11) NOT NULL,
  `medikamentname` varchar(50) NOT NULL,
  `darreichungsform` varchar(50) NOT NULL,
  `wirkstärke` int(11) NOT NULL,
  `einheit` varchar(20) NOT NULL,
  `wirkstoffbezeichnung` varchar(50) NOT NULL,
  `applikationshinweis` varchar(100) NOT NULL,
  `interaction` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `medikament`
--

INSERT INTO `medikament` (`medikament_id`, `medikamentname`, `darreichungsform`, `wirkstärke`, `einheit`, `wirkstoffbezeichnung`, `applikationshinweis`, `interaction`) VALUES
(1, 'ASS 500-1A Pharma', 'peroral', 500, 'Tablette', 'Acetylsalicylsäure', 'Nehmen Sie das Arzneimittel mit Flüssigkeit (z.B. 1 Glas Wasser) ein.', 'IBUFLAM 800mg'),
(2, 'IBUFLAM 800mg', 'peroral', 800, 'Tablette', 'Ibuprofen', 'Nehmen Sie das Arzneimittel mit Flüssigkeit (z.B. 1 Glas Wasser) ein.', 'ASS 500-1A Pharma'),
(3, 'MEDATIN 500ml', 'peroral', 500, 'Packung', 'MEDATIN', '', '');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `patient`
--

CREATE TABLE `patient` (
  `patient_id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `vorname` varchar(30) NOT NULL,
  `anschrift` varchar(50) NOT NULL,
  `telefonnummer` int(11) NOT NULL,
  `aufnahmedatum` date NOT NULL,
  `entlassungsdatum` date NOT NULL,
  `geburtsdatum` date NOT NULL,
  `krankenversicherung` varchar(50) NOT NULL,
  `grund` varchar(100) NOT NULL,
  `zimmer` int(11) NOT NULL,
  `station_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `patient`
--

INSERT INTO `patient` (`patient_id`, `name`, `vorname`, `anschrift`, `telefonnummer`, `aufnahmedatum`, `entlassungsdatum`, `geburtsdatum`, `krankenversicherung`, `grund`, `zimmer`, `station_id`) VALUES
(1, 'Miske', 'Alex', 'Augustdorf', 12345678, '2016-01-01', '2016-01-12', '1989-09-16', 'AOK', 'Aua', 1, 1),
(2, 'Mustermann', 'Max', 'Musterhausen', 12345678, '2016-01-05', '2016-01-27', '1980-10-11', 'AOK', 'Auch Aua', 2, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `personal`
--

CREATE TABLE `personal` (
  `personal_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `vorname` varchar(50) NOT NULL,
  `titel` varchar(50) NOT NULL,
  `anschrift` varchar(50) NOT NULL,
  `telefonnummer` varchar(50) NOT NULL,
  `geburtsdatum` date NOT NULL,
  `station_id` int(11) NOT NULL,
  `rollen_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `personal`
--

INSERT INTO `personal` (`personal_id`, `name`, `vorname`, `titel`, `anschrift`, `telefonnummer`, `geburtsdatum`, `station_id`, `rollen_id`) VALUES
(1, 'Bauer', 'Beate', '', 'Dortmund', '0123456789', '1970-10-11', 1, 2),
(2, 'Gutenberg', 'Gerd', 'Dr.', 'Dortmund', '0123456789', '1963-01-05', 1, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `rollen`
--

CREATE TABLE `rollen` (
  `rollen_id` int(11) NOT NULL,
  `bezeichnung` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `rollen`
--

INSERT INTO `rollen` (`rollen_id`, `bezeichnung`) VALUES
(1, 'Arzt'),
(2, 'Pflegekraft');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `station`
--

CREATE TABLE `station` (
  `station_id` int(11) NOT NULL,
  `zimmeranzahl` int(11) NOT NULL,
  `beschreibung` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `station`
--

INSERT INTO `station` (`station_id`, `zimmeranzahl`, `beschreibung`) VALUES
(1, 30, 'Teststation 1'),
(2, 40, 'Teststation 2');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user`
--

CREATE TABLE `user` (
  `userid` int(11) NOT NULL,
  `pass` varchar(4) NOT NULL,
  `name` varchar(20) NOT NULL,
  `personalid` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `user`
--

INSERT INTO `user` (`userid`, `pass`, `name`, `personalid`) VALUES
(1, '1234', 'ggutenberg', 2),
(2, '1234', 'bbauer', 2);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `verabreichung`
--

CREATE TABLE `verabreichung` (
  `verabreichung_id` int(11) NOT NULL,
  `uhrzeit` time NOT NULL,
  `datum` date NOT NULL,
  `bemerkung` varchar(200) NOT NULL,
  `verordnung_id` int(11) NOT NULL,
  `personal_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `verordnung`
--

CREATE TABLE `verordnung` (
  `verordnung_id` int(11) NOT NULL,
  `applikationszeitpunkt` text NOT NULL,
  `beginn` date DEFAULT NULL,
  `ende` date DEFAULT NULL,
  `patient_id` int(11) NOT NULL,
  `personal_id` int(11) NOT NULL,
  `medikament_id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `dosierung` float NOT NULL,
  `selbstverordnung` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `verordnung`
--

INSERT INTO `verordnung` (`verordnung_id`, `applikationszeitpunkt`, `beginn`, `ende`, `patient_id`, `personal_id`, `medikament_id`, `station_id`, `dosierung`, `selbstverordnung`) VALUES
(1, '[{"tag":"Montag","zeiten": [{"zeit":"08:00"}, {"zeit":"12:00"},{"zeit":"16:00"}, {"zeit":"18:00"}]}]', '2016-01-01', '2016-01-11', 1, 2, 1, 1, 2, 1),
(2, '[{"tag":"Montag","zeiten": [{"zeit":"08:00"}, {"zeit":"16:00"}]},{"tag":"Mittwoch","zeiten": [{"zeit":"08:00"}, {"zeit":"16:00"}]}]', '2016-01-01', '2016-01-11', 2, 2, 2, 1, 1, 1),
(3, '[{"tag":"Montag","zeiten": [{"zeit":"08:00"}, {"zeit":"16:00"}]},{"tag":"Mittwoch","zeiten": [{"zeit":"08:00"}, {"zeit":"16:00"}]}]', '2016-01-01', '2016-01-11', 2, 2, 2, 1, 1, 0),
(16, '[{"tag":"Montag","zeiten":[{"zeit":"08:00"}]},{"tag":"Dienstag","zeiten":[{"zeit":"08:00"}]}]', '2016-01-17', '2016-01-22', 1, 2, 3, 1, 2, 0);

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `medikament`
--
ALTER TABLE `medikament`
  ADD PRIMARY KEY (`medikament_id`);

--
-- Indizes für die Tabelle `patient`
--
ALTER TABLE `patient`
  ADD PRIMARY KEY (`patient_id`);

--
-- Indizes für die Tabelle `personal`
--
ALTER TABLE `personal`
  ADD PRIMARY KEY (`personal_id`);

--
-- Indizes für die Tabelle `rollen`
--
ALTER TABLE `rollen`
  ADD PRIMARY KEY (`rollen_id`);

--
-- Indizes für die Tabelle `station`
--
ALTER TABLE `station`
  ADD PRIMARY KEY (`station_id`);

--
-- Indizes für die Tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`userid`);

--
-- Indizes für die Tabelle `verabreichung`
--
ALTER TABLE `verabreichung`
  ADD PRIMARY KEY (`verabreichung_id`);

--
-- Indizes für die Tabelle `verordnung`
--
ALTER TABLE `verordnung`
  ADD PRIMARY KEY (`verordnung_id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `medikament`
--
ALTER TABLE `medikament`
  MODIFY `medikament_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT für Tabelle `patient`
--
ALTER TABLE `patient`
  MODIFY `patient_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT für Tabelle `personal`
--
ALTER TABLE `personal`
  MODIFY `personal_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT für Tabelle `rollen`
--
ALTER TABLE `rollen`
  MODIFY `rollen_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT für Tabelle `station`
--
ALTER TABLE `station`
  MODIFY `station_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT für Tabelle `user`
--
ALTER TABLE `user`
  MODIFY `userid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT für Tabelle `verabreichung`
--
ALTER TABLE `verabreichung`
  MODIFY `verabreichung_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `verordnung`
--
ALTER TABLE `verordnung`
  MODIFY `verordnung_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
