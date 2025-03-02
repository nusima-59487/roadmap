-- Active: 1717860514531@@10.0.0.237@3306@rdmap2
create database rdmap2; 
use rdmap2; 
drop DATABASE rdmap1; 

#1
CREATE TABLE IF NOT EXISTS Nodes (
    node_id INT NOT NULL AUTO_INCREMENT,
    coord_x INT NOT NULL, 
    coord_z INT NOT NULL,
    name VARCHAR(64),
    aux_node BOOLEAN NOT NULL DEFAULT FALSE comment "Auxiliary node",
    connection_n INT comment "Segment of North (if not aux)", 
    connection_ne INT comment "Segment of North (if not aux)", 
    connection_e INT comment "Segment of North (if not aux)", 
    connection_se INT comment "Segment of North (if not aux)", 
    connection_s INT comment "Segment of North (if not aux)", 
    connection_sw INT comment "Segment of North (if not aux)", 
    connection_w INT comment "Segment of North (if not aux)", 
    connection_nw INT comment "Segment of North (if not aux)",
    has_stop_sign BOOLEAN comment "Has stop sign (if not aux)",
    has_traffic_light BOOLEAN comment "Has traffic light (if not aux)", 
    create_time DATETIME NOT NULL DEFAULT NOW(),
    last_update_username VARCHAR(64) NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT NOW(),
    PRIMARY KEY (node_id)
);

#2
CREATE TABLE IF NOT EXISTS Segments (
    segment_id INT NOT NULL AUTO_INCREMENT,
    starting_node INT NOT NULL,
    ending_node INT NOT NULL,
    is_one_way BOOLEAN NOT NULL DEFAULT FALSE, 
    lane_count_forward INT NOT NULL DEFAULT 2,
    lane_count_backward INT DEFAULT 2,
    speed_limit INT NOT NULL DEFAULT 50,
    road_type VARCHAR(15) NOT NULL DEFAULT "Road",
    create_time DATETIME NOT NULL DEFAULT NOW(),
    last_update_username VARCHAR(64) NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT NOW(),
    PRIMARY KEY (segment_id),
    Foreign Key (starting_node) REFERENCES nodes(node_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    Foreign Key (ending_node) REFERENCES nodes(node_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

#3
CREATE TABLE SegmentAlignments (
    segment_id int not null,
    aux_node_id int not null,
    alignment_index int not null,
    create_time DATETIME NOT NULL DEFAULT NOW(),
    last_update_username VARCHAR(64) NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT NOW(),
    Foreign Key (segment_id) REFERENCES Segments(segment_id),
    Foreign Key (aux_node_id) REFERENCES Nodes(node_id)
)

#3
ALTER TABLE nodes
    ADD FOREIGN KEY (connection_n) REFERENCES segments(segment_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    ADD FOREIGN KEY (connection_ne) REFERENCES segments(segment_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    ADD FOREIGN KEY (connection_e) REFERENCES segments(segment_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    ADD FOREIGN KEY (connection_se) REFERENCES segments(segment_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    ADD FOREIGN KEY (connection_s) REFERENCES segments(segment_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    ADD FOREIGN KEY (connection_sw) REFERENCES segments(segment_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    ADD FOREIGN KEY (connection_w) REFERENCES segments(segment_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    ADD FOREIGN KEY (connection_nw) REFERENCES segments(segment_id)
        ON UPDATE CASCADE ON DELETE SET NULL;

#4
CREATE PROCEDURE add_main_segment (
    IN start_node INT,
    IN start_node_dir VARCHAR(2),
    IN end_node INT,
    IN end_node_dir VARCHAR(2),
    IN lanes_forward INT,
    IN lanes_backward INT,
    IN speed INT,
    IN type VARCHAR(15),
    IN create_user VARCHAR(64)
) BEGIN
    DECLARE segment_key INT;
    IF (select aux_node from nodes where node_id = start_node) = 0 THEN update nodes set aux_node = TRUE where node_id = start_node; END IF; 
    IF (select aux_node from nodes where node_id = end_node) = 0 THEN update nodes set aux_node = TRUE where node_id = end_node; END IF; 
    INSERT INTO segments (starting_node, ending_node, lane_count_forward, lane_count_backward, road_type, speed_limit, last_update_username)
        VALUES (start_node, end_node, lanes_forward, lanes_backward, type, speed, create_user); 
    SET segment_key = LAST_INSERT_ID(); 
    IF start_node_dir = "n" THEN UPDATE nodes SET connection_n = segment_key WHERE node_id = start_node;
    ELSEIF start_node_dir = "ne" THEN UPDATE nodes SET connection_ne = segment_key WHERE node_id = start_node;
    ELSEIF start_node_dir = "e" THEN UPDATE nodes SET connection_e = segment_key WHERE node_id = start_node;
    ELSEIF start_node_dir = "se" THEN UPDATE nodes SET connection_se = segment_key WHERE node_id = start_node;
    ELSEIF start_node_dir = "s" THEN UPDATE nodes SET connection_s = segment_key WHERE node_id = start_node;
    ELSEIF start_node_dir = "sw" THEN UPDATE nodes SET connection_sw = segment_key WHERE node_id = start_node;
    ELSEIF start_node_dir = "w" THEN UPDATE nodes SET connection_w = segment_key WHERE node_id = start_node;
    ELSEIF start_node_dir = "nw" THEN UPDATE nodes SET connection_nw = segment_key WHERE node_id = start_node;
    END IF; 
    
    IF end_node_dir = "n" THEN UPDATE nodes SET connection_n = segment_key WHERE node_id = end_node;
    ELSEIF end_node_dir = "ne" THEN UPDATE nodes SET connection_ne = segment_key WHERE node_id = end_node;
    ELSEIF end_node_dir = "e" THEN UPDATE nodes SET connection_e = segment_key WHERE node_id = end_node;
    ELSEIF end_node_dir = "se" THEN UPDATE nodes SET connection_se = segment_key WHERE node_id = end_node;
    ELSEIF end_node_dir = "s" THEN UPDATE nodes SET connection_s = segment_key WHERE node_id = end_node;
    ELSEIF end_node_dir = "sw" THEN UPDATE nodes SET connection_sw = segment_key WHERE node_id = end_node;
    ELSEIF end_node_dir = "w" THEN UPDATE nodes SET connection_w = segment_key WHERE node_id = end_node;
    ELSEIF end_node_dir = "nw" THEN UPDATE nodes SET connection_nw = segment_key WHERE node_id = end_node;
    END IF; 
END;  

#5
CREATE PROCEDURE create_aux_node (
    IN segment INT, 
    IN node_index INT,
    IN coordx INT,
    IN coordz INT,
    IN create_user VARCHAR(64)
) BEGIN
    
END;


# TEST
SELECT * FROM nodes; 
INSERT INTO nodes (coord_x, coord_z, aux_node, last_update_username)
VALUES (100, 200, TRUE, "Console");
INSERT INTO nodes (coord_x, coord_z, aux_node, last_update_username)
VALUES (300, 100, TRUE, "Console");
INSERT INTO segments (starting_node, ending_node, last_update_username)
VALUES (1, 2, "Console"); 
DROP PROCEDURE add_main_segment; 