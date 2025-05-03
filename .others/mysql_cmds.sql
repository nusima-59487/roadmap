-- Active: 1744938217411@@10.0.0.93@3306@rdmaptest
create database rdmaptest; 
use rdmaptest; 
drop DATABASE rdmaptest; 

# Initial SQL commands for the database
### 1: create nodes table
CREATE TABLE IF NOT EXISTS Nodes (
    node_id INT NOT NULL AUTO_INCREMENT,
    coord_x INT NOT NULL, 
    coord_z INT NOT NULL,
    name VARCHAR(64),
    aux_node BOOLEAN NOT NULL DEFAULT FALSE COMMENT "Auxiliary node",
    has_stop_sign BOOLEAN COMMENT "Has stop sign (if not aux)",
    has_traffic_light BOOLEAN COMMENT "Has traffic light (if not aux)", 
    create_time DATETIME NOT NULL DEFAULT NOW(),
    last_update_user_uuid VARCHAR(36) NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT NOW(),
    PRIMARY KEY (node_id)
);

### 2: create segments table
CREATE TABLE IF NOT EXISTS Segments (
    segment_id INT NOT NULL AUTO_INCREMENT,
    starting_node INT NOT NULL,
    ending_node INT NOT NULL,
    y_index TINYINT UNSIGNED NOT NULL DEFAULT 10,
    is_one_way BOOLEAN NOT NULL DEFAULT FALSE, 
    lane_count_forward INT NOT NULL DEFAULT 2,
    lane_count_backward INT DEFAULT 2,
    speed_limit INT NOT NULL DEFAULT 50,
    road_type VARCHAR(15) NOT NULL DEFAULT "Road",
    name VARCHAR(256), 
    create_time DATETIME NOT NULL DEFAULT NOW(),
    last_update_user_uuid VARCHAR(36) NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT NOW(),
    PRIMARY KEY (segment_id),
    FOREIGN KEY (starting_node) REFERENCES Nodes(node_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (ending_node) REFERENCES Nodes(node_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

### 3: create segment and aux node relations table
CREATE TABLE SegmentAlignments ( /*aux node connections*/
    segment_id INT NOT NULL, 
    aux_node_id INT NOT NULL,
    alignment_index INT NOT NULL,
    lod_level INT NOT NULL DEFAULT 1, /* smaller number = loaded when zoomed in smaller */
    create_time DATETIME NOT NULL DEFAULT NOW(),
    last_update_user_uuid VARCHAR(36) NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT NOW(),
    Foreign Key (segment_id) REFERENCES Segments(segment_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    Foreign Key (aux_node_id) REFERENCES Nodes(node_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (segment_id, alignment_index)
);

### 4: create main node connections table
CREATE TABLE NodeConnections ( /*Main node connections*/
    node_id INT NOT NULL,
    direction ENUM('n', 'ne', 'e', 'se', 's', 'sw', 'w', 'nw', 'none') NOT NULL DEFAULT 'none', 
    segment_id INT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT NOW(),
    last_update_user_uuid VARCHAR(36) NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT NOW(),
    FOREIGN KEY (node_id) REFERENCES Nodes(node_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (segment_id) REFERENCES Segments(segment_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (node_id, direction)
);

# Methods
### static void DBMethods.addSegment(int startNode, String startNodeDir, int endNode, String endNodeDir, int lanesForward, int lanesBackward, int speed, String type, UUID createUser)
CREATE PROCEDURE add_segment (
    IN start_node INT,
    IN start_node_dir VARCHAR(2),
    IN end_node INT,
    IN end_node_dir VARCHAR(2),
    IN lanes_forward INT,
    IN lanes_backward INT,
    IN speed INT,
    IN type VARCHAR(15),
    IN create_user VARCHAR(36)
) BEGIN
    DECLARE segment_key INT;
    IF (select aux_node from nodes where node_id = start_node) = 0 THEN update nodes set aux_node = TRUE where node_id = start_node; END IF; /* change nodes into main if aux */
    IF (select aux_node from nodes where node_id = end_node) = 0 THEN update nodes set aux_node = TRUE where node_id = end_node; END IF; /* change nodes into main if aux */
    INSERT INTO segments (starting_node, ending_node, lane_count_forward, lane_count_backward, road_type, speed_limit, last_update_user_uuid)
        VALUES (start_node, end_node, lanes_forward, lanes_backward, type, speed, create_user); 
    SET segment_key = LAST_INSERT_ID(); 
    INSERT INTO nodeconnections (node_id, direction, segment_id, last_update_user_uuid)
        VALUES (start_node, start_node_dir, segment_key, create_user);
    INSERT INTO nodeconnections (node_id, direction, segment_id, last_update_user_uuid)
        VALUES (end_node, end_node_dir, segment_key, create_user);
END;  

### static void DBMethods.createAuxNode(int segment, int nodeIndex, int coordx, int coordz, UUID createUser)
# Add anchor point for segemnts
CREATE PROCEDURE create_aux_node (
    IN segment INT, 
    IN node_index INT,
    IN coordx INT,
    IN coordz INT,
    IN create_user VARCHAR(36)
) BEGIN
    DECLARE node_key INT;
    INSERT INTO nodes (coord_x, coord_z, aux_node, last_update_user_uuid)
        VALUES (coordx, coordz, TRUE, create_user);
    SET node_key = LAST_INSERT_ID();
    INSERT INTO segmentalignments (segment_id, aux_node_id, alignment_index, last_update_user_uuid)
        VALUES (segment, node_key, node_index, create_user);
END;

### static Node DBMethods.getNodeIdFromCoords(int coordx, int coordz) 
# get node using coords
CREATE PROCEDURE get_node_id_from_coords (
    IN coordx INT, 
    IN coordz INT,
    OUT id INT
) BEGIN 
    SELECT node_id INTO id FROM Nodes 
        WHERE Nodes.coord_x = coordx
        AND Nodes.coord_z = coordz; 
END; 


# TEST
SELECT * FROM Nodes; 
INSERT INTO Nodes (coord_x, coord_z, aux_node, last_update_user_uuid) VALUES 
    (0, 0, FALSE, "console"), 
    (0, 10, FALSE, "console"), 
    (0, -10, FALSE, "console"), 
    (7, 15, TRUE, "console"), 
    (10, 15, FALSE, "console"), 
    (10, 0, FALSE, "console"), 
    (13, 0, TRUE, "console"), 
    (20, 15, TRUE, "console"),
    (20, 7, FALSE, "console"), 
    (20, -10, FALSE, "console");
    
CALL get_node_id_from_coords (10, 15, @ids); # return empty set if no matches
SELECT * FROM `Nodes` WHERE node_id = @ids; # return empty set if no matches

INSERT INTO Segments (starting_node, ending_node, last_update_user_uuid) VALUES 
    (1, 2, "4c15b77c-7a44-46c7-953b-ff0d1c67a653");