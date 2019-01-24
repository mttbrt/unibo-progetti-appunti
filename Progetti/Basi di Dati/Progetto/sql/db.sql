/*Creazione del database */

CREATE DATABASE IF NOT EXISTS gitr ;
USE gitr;

/* Tabella degli utenti */
CREATE TABLE user (
  username VARCHAR(40) PRIMARY KEY,
  password VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

/* Tabella dei progetti */
CREATE TABLE project (
  INDEX creator (creator),
  name VARCHAR(40) PRIMARY KEY,
  creator VARCHAR(40),
  creation_date DATE NOT NULL,
  FOREIGN KEY (creator) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

/* Repository */
CREATE TABLE repository (
  INDEX project_name (project_name),
  id VARCHAR(40) PRIMARY KEY,
  url VARCHAR(250) NOT NULL,
  project_name VARCHAR(40),
  FOREIGN KEY (project_name) REFERENCES project(name) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

/* Assoccia utenti e repo */
CREATE TABLE access (
  INDEX username (username),
  INDEX repo_id (repo_id),
  username VARCHAR(40),
  repo_id VARCHAR(40),
  PRIMARY KEY (username, repo_id),
  FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (repo_id) REFERENCES repository(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

/* Tiene traccia delle azioni */
CREATE TABLE log (
  INDEX username (username),
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  date DATE,
  time TIME,
  username VARCHAR(40),
  action ENUM('Create Project','Delete Project','Create Branch','Delete Branch','Push','Merge','Commit','Fork Branch', 'Join Project', 'New User', 'Delete User'),
  element VARCHAR(100),
  FOREIGN KEY (username) REFERENCES user(username) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

/* Branch master pubblici */
CREATE TABLE master (
  INDEX repo_id (repo_id),
  repo_id VARCHAR(40) PRIMARY KEY,
  url VARCHAR(250) NOT NULL,
  version INTEGER DEFAULT 0,
  FOREIGN KEY (repo_id) REFERENCES repository(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

/* Branch develop pubblici */
CREATE TABLE develop (
  INDEX master_id (master_id),
  master_id VARCHAR(40) PRIMARY KEY,
  url VARCHAR(250) NOT NULL,
  version INTEGER DEFAULT 0,
  FOREIGN KEY (master_id) REFERENCES master(repo_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

/* Feature branches pubblici */
CREATE TABLE feature_pub (
  INDEX develop_id (develop_id),
  develop_id VARCHAR(40),
  name VARCHAR(40),
  url VARCHAR(250) NOT NULL,
  notes TEXT,
  version INTEGER DEFAULT 0,
  PRIMARY KEY (develop_id, name),
  FOREIGN KEY (develop_id) REFERENCES develop(master_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

/* Feature branches privati */
CREATE TABLE feature_pri (
  INDEX feature_pub_id (feature_pub_id),
  INDEX feature_pub_name (feature_pub_name),
  INDEX username (username),
  feature_pub_id VARCHAR(40),
  feature_pub_name VARCHAR(40),
  username VARCHAR(40),
  url VARCHAR(250) NOT NULL,
  version INTEGER DEFAULT 0,
  PRIMARY KEY (feature_pub_id, feature_pub_name, username),
  FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (feature_pub_id, feature_pub_name) REFERENCES feature_pub(develop_id, name) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

/* Release branches pubblici */
CREATE TABLE release_pub (
  INDEX develop_id (develop_id),
  develop_id VARCHAR(40),
  url VARCHAR(250) NOT NULL,
  notes TEXT,
  version INTEGER DEFAULT 0,
  PRIMARY KEY (develop_id),
  FOREIGN KEY (develop_id) REFERENCES develop(master_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

/* Release branches privati */
CREATE TABLE release_pri (
  INDEX release_pub_id (release_pub_id),
  INDEX username (username),
  release_pub_id VARCHAR(40),
  username VARCHAR(40),
  url VARCHAR(250) NOT NULL,
  version INTEGER DEFAULT 0,
  PRIMARY KEY (release_pub_id, username),
  FOREIGN KEY (release_pub_id) REFERENCES release_pub(develop_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (username) REFERENCES user(username) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

/* Hotfix branches pubblici */
CREATE TABLE hotfix_pub (
  INDEX develop_id (develop_id),
  develop_id VARCHAR(40),
  url VARCHAR(250) NOT NULL,
  notes TEXT,
  version INTEGER DEFAULT 0,
  PRIMARY KEY (develop_id),
  FOREIGN KEY (develop_id) REFERENCES develop(master_id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

/* Hotfix branches privati */
CREATE TABLE hotfix_pri (
  INDEX hotfix_pub_id (hotfix_pub_id),
  INDEX username (username),
  hotfix_pub_id VARCHAR(40),
  username VARCHAR(40),
  url VARCHAR(250) NOT NULL,
  version INTEGER DEFAULT 0,
  PRIMARY KEY (hotfix_pub_id, username),
  FOREIGN KEY (hotfix_pub_id) REFERENCES hotfix_pub(develop_id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (username) REFERENCES user(username) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

/**** TRIGGERS ****/

CREATE TRIGGER log_create_user AFTER INSERT ON user FOR EACH ROW
  CALL addLogRow(NEW.username, 'New user', CONCAT('+ ', NEW.username));

CREATE TRIGGER log_delete_user BEFORE DELETE ON user FOR EACH ROW
  CALL addLogRow(OLD.username, 'Delete User', CONCAT('- ', OLD.username));

CREATE TRIGGER log_create_project AFTER INSERT ON project FOR EACH ROW
  CALL addLogRow(NEW.creator, 'Create Project', NEW.name);

CREATE TRIGGER log_delete_project AFTER DELETE ON project FOR EACH ROW
  CALL addLogRow(OLD.creator, 'Delete Project', OLD.name);

CREATE TRIGGER log_join_project AFTER INSERT ON access FOR EACH ROW
  CALL addLogRow(NEW.username, 'Join Project',
    (SELECT project_name FROM repository WHERE id = NEW.repo_id));

CREATE TRIGGER log_fork_feature_branch_pri AFTER INSERT ON feature_pri FOR EACH ROW
  CALL addLogRow(NEW.username, 'Fork Branch', CONCAT ('Feature -', NEW.feature_pub_name, '- from project -',
    (SELECT project_name FROM repository WHERE id = NEW.feature_pub_id), '-'));

CREATE TRIGGER log_commit_feature_branch_pri AFTER UPDATE ON feature_pri FOR EACH ROW
  CALL addLogRow(NEW.username, 'Commit', CONCAT('Feature -', NEW.feature_pub_name, '- v', NEW.version,' from project -',
    (SELECT project_name FROM repository WHERE id = NEW.feature_pub_id), '-'));

CREATE TRIGGER log_fork_release_branch AFTER INSERT ON release_pri FOR EACH ROW
  CALL addLogRow(NEW.username, 'Fork Branch', CONCAT('Release from project -',
    (SELECT project_name FROM repository WHERE id = NEW.release_pub_id), '-'));

CREATE TRIGGER log_commit_release_branch AFTER UPDATE ON release_pri FOR EACH ROW
  CALL addLogRow(NEW.username, 'Commit', CONCAT('Release v', NEW.version, ' from project -',
    (SELECT project_name FROM repository WHERE id = NEW.release_pub_id), '-'));

CREATE TRIGGER log_fork_hotfix_branch AFTER INSERT ON hotfix_pri FOR EACH ROW
  CALL addLogRow(NEW.username, 'Fork Branch', CONCAT ('Hotfix from project -',
    (SELECT project_name FROM repository WHERE id = NEW.hotfix_pub_id), '-'));

CREATE TRIGGER log_commit_hotfix_branch AFTER UPDATE ON hotfix_pri FOR EACH ROW
  CALL addLogRow(NEW.username, 'Commit', CONCAT ('Hotfix v', NEW.version, ' from project -',
    (SELECT project_name FROM repository WHERE id = NEW.hotfix_pub_id), '-'));

/**** STORED PROCEDURES ***/

DELIMITER |
CREATE PROCEDURE grantAccessToRepo(IN in_username VARCHAR(40), IN in_repo_id VARCHAR(40))
  BEGIN
    DECLARE accessCount INT;
    SELECT COUNT(*) INTO accessCount FROM access WHERE username=in_username AND repo_id=in_repo_id;
    IF accessCount = 0 THEN
      INSERT INTO access(username, repo_id) VALUES(in_username, in_repo_id);
    END IF;
  END;
|

CREATE PROCEDURE addLogRow(
  IN username VARCHAR(40),
  IN action ENUM('Create Project','Delete Project','Create Branch','Delete Branch','Push','Merge','Commit','Fork Branch', 'Join Project', 'New User', 'Delete User'),
  IN element VARCHAR (100))
  BEGIN
    INSERT INTO log(date, time, username, action, element) VALUES (CURRENT_DATE(), CURRENT_TIME(), username, action, element);
  END;
|

CREATE PROCEDURE getProjectBranches(IN projectName VARCHAR(40),
	OUT release_exists BOOLEAN, OUT hotfix_exists BOOLEAN)
  BEGIN
    DECLARE repo_id VARCHAR(40);
    SELECT id INTO repo_id FROM repository WHERE project_name=projectName;

  	SELECT IF((SELECT develop_id FROM release_pub WHERE develop_id=repo_id) IS NULL, 0, 1) INTO release_exists;
  	SELECT IF((SELECT develop_id FROM hotfix_pub WHERE develop_id=repo_id) IS NULL, 0, 1) INTO hotfix_exists;
  	CREATE TEMPORARY TABLE features_names SELECT name FROM feature_pub WHERE develop_id=repo_id;
  END;
|

CREATE PROCEDURE getFeatureBranchVersion( IN repo_id VARCHAR(40),
  IN br_name VARCHAR(40), IN public BOOLEAN, IN br_username VARCHAR(40),
  OUT version_num INTEGER)
  BEGIN
    /* private*/
    IF (public = 0) THEN
      SELECT version INTO version_num FROM feature_pri WHERE feature_pub_id=repo_id AND feature_pub_name=br_name AND username=br_username;
    /*public*/
    ELSE
      SELECT version INTO version_num FROM feature_pub WHERE develop_id=repo_id AND name=br_name;
    END IF;
  END;
|

CREATE PROCEDURE getReleaseBranchVersion( IN repo_id VARCHAR(40),
   IN public BOOLEAN, IN br_username VARCHAR(40), OUT version_num INTEGER)
  BEGIN
    /* private*/
    IF (public = 0) THEN
      SELECT version INTO version_num FROM release_pri WHERE release_pub_id=repo_id AND username=br_username;
    /*public*/
    ELSE
      SELECT version INTO version_num FROM release_pub WHERE develop_id=repo_id;
    END IF;
  END;
|

CREATE PROCEDURE getHotfixBranchVersion( IN repo_id VARCHAR(40),
   IN public BOOLEAN, IN br_username VARCHAR(40), OUT version_num INTEGER)
  BEGIN
    /* private*/
    IF (public = 0) THEN
      SELECT version INTO version_num FROM hotfix_pri WHERE hotfix_pub_id=repo_id AND username=br_username;
    /*public*/
    ELSE
      SELECT version INTO version_num FROM hotfix_pub WHERE develop_id=repo_id;
    END IF;
  END;
|

CREATE PROCEDURE deleteBranchPub(IN dev_id VARCHAR(40), IN branch INTEGER,
  IN br_name VARCHAR(40), IN username VARCHAR(40), OUT deleteURL VARCHAR(250))
  BEGIN
    DECLARE temp VARCHAR(250);
    IF branch = 1 THEN
      SELECT url INTO temp FROM feature_pub WHERE develop_id=dev_id AND name=br_name;
      DELETE FROM feature_pub WHERE develop_id=dev_id AND name=br_name;
      IF (ROW_COUNT() > 0) THEN
        CALL addLogRow(username, "Delete Branch", CONCAT("[pub] ", br_name));
      END IF;
    ELSEIF branch = 2 THEN
      SELECT url INTO temp FROM release_pub WHERE develop_id=dev_id;
      DELETE FROM release_pub WHERE develop_id=dev_id;
      IF (ROW_COUNT() > 0) THEN
        CALL addLogRow(username, "Delete Branch", "[pub] Release");
      END IF;
    ELSEIF branch = 3 THEN
      SELECT url INTO temp FROM hotfix_pub WHERE develop_id=dev_id;
      DELETE FROM hotfix_pub WHERE develop_id=dev_id;
      IF (ROW_COUNT() > 0) THEN
        CALL addLogRow(username, "Delete Branch", "[pub] Hotfix");
      END IF;
    ELSE
      SELECT 'ERROR: wrong branch number' INTO temp;
    END IF;
    SELECT temp INTO deleteURL;
  END;
|

CREATE PROCEDURE deleteBranchPri(IN dev_id VARCHAR(40), IN branch INTEGER,
  IN br_name VARCHAR(40), IN br_username VARCHAR(40), OUT deleteURL VARCHAR(250))
  BEGIN
    DECLARE temp VARCHAR(250);
    IF branch = 1 THEN
      SELECT url INTO temp FROM feature_pri WHERE feature_pub_id=dev_id AND feature_pub_name=br_name AND username=br_username;
      DELETE FROM feature_pri WHERE feature_pub_id=dev_id AND feature_pub_name=br_name AND username=br_username;
      IF (ROW_COUNT() > 0) THEN
        CALL addLogRow(br_username, "Delete Branch", CONCAT("[pri] ", br_name));
      END IF;
    ELSEIF branch = 2 THEN
      SELECT url INTO temp FROM release_pri WHERE release_pub_id=dev_id AND username=br_username;
      DELETE FROM release_pri WHERE release_pub_id=dev_id AND username=br_username;
      IF (ROW_COUNT() > 0) THEN
        CALL addLogRow(br_username, "Delete Branch", "[pri] Release");
      END IF;
    ELSEIF branch = 3 THEN
      SELECT url INTO temp FROM hotfix_pri WHERE hotfix_pub_id=dev_id AND username=br_username;
      DELETE FROM hotfix_pri WHERE hotfix_pub_id=dev_id AND username=br_username;
      IF (ROW_COUNT() > 0) THEN
        CALL addLogRow(br_username, "Delete Branch", "[pri] Hotfix");
      END IF;
    ELSE
      SELECT 'ERROR: wrong branch number' INTO temp;
    END IF;
    SELECT temp INTO deleteURL;
  END;
|

CREATE PROCEDURE commitFeatureWork(IN proj_name VARCHAR(40), IN name VARCHAR(40),
  IN in_username VARCHAR(40), OUT commitURL VARCHAR(250), OUT next_version INTEGER)
  BEGIN
    DECLARE new_version INT;
    DECLARE repo_id VARCHAR(40);
    SELECT id INTO repo_id FROM repository WHERE project_name=proj_name;
    SELECT 1 + version INTO new_version FROM feature_pri WHERE feature_pub_id=repo_id
      AND feature_pub_name=name
      AND username=in_username;
    UPDATE feature_pri SET version=new_version WHERE feature_pub_id=repo_id
      AND feature_pub_name=name
      AND username=in_username;
    SELECT url INTO commitURL FROM feature_pri WHERE feature_pub_id=repo_id
      AND feature_pub_name=name
      AND username=in_username;
    SELECT new_version INTO next_version;
  END;
|

CREATE PROCEDURE commitReleaseWork(IN proj_name VARCHAR(40), IN in_username VARCHAR(40),
  OUT commitURL VARCHAR(250), OUT next_version INTEGER)
  BEGIN
    DECLARE new_version INT;
    DECLARE repo_id VARCHAR(40);
    SELECT id INTO repo_id FROM repository WHERE project_name=proj_name;
    SELECT 1 + version INTO new_version FROM release_pri WHERE release_pub_id=repo_id
      AND username=in_username;
    UPDATE release_pri SET version=new_version WHERE release_pub_id=repo_id
      AND username=in_username;
    SELECT url INTO commitURL FROM release_pri WHERE release_pub_id=repo_id
      AND username=in_username;
    SELECT new_version INTO next_version;
  END;
|

CREATE PROCEDURE commitHotfixWork(IN proj_name VARCHAR(40), IN in_username VARCHAR(40),
  OUT commitURL VARCHAR(250), OUT next_version INTEGER)
  BEGIN
    DECLARE new_version INTEGER;
    DECLARE repo_id VARCHAR(40);
    SELECT id INTO repo_id FROM repository WHERE project_name=proj_name;
    SELECT 1 + version INTO new_version FROM hotfix_pri WHERE hotfix_pub_id=repo_id
      AND username=in_username;
    UPDATE hotfix_pri SET version=new_version WHERE hotfix_pub_id=repo_id
      AND username=in_username;
    SELECT url INTO commitURL FROM hotfix_pri WHERE hotfix_pub_id=repo_id
      AND username=in_username;
    SELECT new_version INTO next_version;
  END;
|

CREATE PROCEDURE restoreFeatureBranchVersion(IN repo_id VARCHAR(40),
	IN in_name VARCHAR (40), IN in_version INT, IN public BOOLEAN, IN in_username VARCHAR(40),
	OUT restoreURL VARCHAR(250))
	BEGIN
		IF public = 1 THEN
			UPDATE feature_pub SET version=in_version WHERE develop_id=repo_id AND name=in_name;
			SELECT url INTO restoreURL FROM feature_pub WHERE develop_id=repo_id AND name=in_name;
		ELSEIF public = 0 THEN
			UPDATE feature_pri SET version=in_version WHERE feature_pub_id=repo_id AND feature_pub_name=in_name AND username=in_username;
			SELECT url INTO restoreURL FROM feature_pri WHERE feature_pub_id=repo_id AND feature_pub_name=in_name AND username=in_username;
		ELSE SELECT 'ERROR: public is not boolean' INTO restoreURL;
		END IF;
	END;
|

CREATE PROCEDURE restoreReleaseBranchVersion(IN repo_id VARCHAR(40),
	IN in_version INT, IN public BOOLEAN, IN in_username VARCHAR(40),
	OUT restoreURL VARCHAR(250))
	BEGIN
		IF public = 1 THEN
			UPDATE release_pub SET version=in_version WHERE develop_id=repo_id;
			SELECT url INTO restoreURL FROM release_pub WHERE develop_id=repo_id;
		ELSEIF public = 0 THEN
			UPDATE release_pri SET version=in_version WHERE release_pub_id=repo_id AND username=in_username;
			SELECT url INTO restoreURL FROM release_pri WHERE release_pub_id=repo_id AND username=in_username;
		ELSE SELECT 'ERROR: public is not boolean' INTO restoreURL;
		END IF;
	END;
|

CREATE PROCEDURE restoreHotfixBranchVersion(IN repo_id VARCHAR(40),
	IN in_version INT, IN public BOOLEAN, IN in_username VARCHAR(40),
	OUT restoreURL VARCHAR(250))
	BEGIN
		IF public = 1 THEN
			UPDATE hotfix_pub SET version=in_version WHERE develop_id=repo_id;
			SELECT url INTO restoreURL FROM hotfix_pub WHERE develop_id=repo_id;
		ELSEIF public = 0 THEN
			UPDATE hotfix_pri SET version=in_version WHERE hotfix_pub_id=repo_id AND username=in_username;
			SELECT url INTO restoreURL FROM hotfix_pri WHERE hotfix_pub_id=repo_id AND username=in_username;
		ELSE SELECT 'ERROR: public is not boolean' INTO restoreURL;
		END IF;
	END;
|

CREATE PROCEDURE restoreDevelopBranchVersion(IN repo_id VARCHAR(40),
	IN in_version INT, OUT restoreURL VARCHAR(250))
	BEGIN
		UPDATE develop SET version=in_version WHERE master_id=repo_id;
		SELECT url INTO restoreURL FROM develop WHERE master_id=repo_id;
	END;
|

CREATE PROCEDURE restoreMasterBranchVersion(IN repo_id VARCHAR(40),
	IN in_version INT, OUT restoreURL VARCHAR(250))
	BEGIN
		UPDATE master SET version=in_version WHERE repo_id=repo_id;
		SELECT url INTO restoreURL FROM master WHERE repo_id=repo_id;
	END;
|

CREATE PROCEDURE createProject(IN in_username VARCHAR(40), IN in_project_name VARCHAR(40),
  IN now BIGINT, OUT setupurl VARCHAR(250))
  BEGIN
    DECLARE new_repo_id VARCHAR(40);
    DECLARE new_repo_url VARCHAR(250);
    DECLARE master_url VARCHAR(250);
    DECLARE develop_url VARCHAR(250);
    INSERT INTO project(name, creator, creation_date) VALUES(in_project_name, in_username, CURRENT_TIMESTAMP());
    SET new_repo_id = SHA(CONCAT(in_username, in_project_name, now));
    SET new_repo_url = CONCAT('./Users/', in_username,'/projects/',new_repo_id, '/' );
    INSERT INTO repository(id, url, project_name)  VALUES(new_repo_id, new_repo_url, in_project_name);
    CALL grantAccessToRepo(in_username, new_repo_id);
    SET master_url = CONCAT(new_repo_url, 'master/');
    INSERT INTO master(repo_id, url) VALUES(new_repo_id, master_url);
    SET develop_url = CONCAT(new_repo_url, 'develop/');
    INSERT INTO develop(master_id, url) VALUES(new_repo_id, develop_url);
    SET setupurl = new_repo_url;
  END;
|

CREATE PROCEDURE deleteProject(IN in_username VARCHAR(40), IN in_project_name VARCHAR(40),
  OUT deleteurl VARCHAR(250))
  BEGIN
    SELECT url INTO deleteurl FROM repository WHERE project_name=in_project_name;
    DELETE FROM project WHERE name=in_project_name;
  END;
|

CREATE PROCEDURE joinFeatureBranch(IN in_project_name VARCHAR(40), IN in_name VARCHAR(40),
  IN in_username VARCHAR(40), OUT feature_url VARCHAR(250), OUT joinurl VARCHAR(250))
  BEGIN
    DECLARE baseurl VARCHAR(250);
    DECLARE repo_id VARCHAR(40);
    DECLARE tempurl VARCHAR(250);
    DECLARE tempversion INT;
    SELECT id INTO repo_id FROM repository WHERE project_name=in_project_name;
    SET baseurl = CONCAT('./Users/', in_username, '/workbench/', repo_id, '/feature_', in_name, '/');
    CALL grantAccessToRepo(in_username, repo_id);
    INSERT INTO feature_pri(feature_pub_id, feature_pub_name, username, url) VALUES(repo_id, in_name, in_username, baseurl);
    SELECT url, version INTO tempurl, tempversion  FROM feature_pub WHERE develop_id=repo_id AND name=in_name;
    SET joinurl = CONCAT(tempurl, '.v_', tempversion, '/');
    SET feature_url = baseurl;
  END;
|

CREATE PROCEDURE joinReleaseBranch(IN in_project_name VARCHAR(40), IN in_username VARCHAR(40),
  OUT feature_url VARCHAR(250), OUT joinurl VARCHAR(250))
  BEGIN
    DECLARE baseurl VARCHAR(250);
    DECLARE repo_id VARCHAR(40);
    DECLARE tempurl VARCHAR(250);
    DECLARE tempversion INT;
    SELECT id INTO repo_id FROM repository WHERE project_name=in_project_name;
    SET baseurl = CONCAT('./Users/', in_username, '/workbench/', repo_id,'/release/');
    CALL grantAccessToRepo(in_username, repo_id);
    INSERT INTO release_pri(release_pub_id, username, url) VALUES(repo_id, in_username, baseurl);
    SELECT url, version INTO tempurl, tempversion  FROM release_pub WHERE develop_id=repo_id;
    SET joinurl = CONCAT(tempurl, '.v_', tempversion, '/');
    SET feature_url = baseurl;
  END;
|

CREATE PROCEDURE joinHotfixBranch(IN in_project_name VARCHAR(40), IN in_username VARCHAR(40),
  OUT feature_url VARCHAR(250), OUT joinurl VARCHAR(250))
  BEGIN
    DECLARE baseurl VARCHAR(250);
    DECLARE repo_id VARCHAR(40);
    DECLARE tempurl VARCHAR(250);
    DECLARE tempversion INT;
    SELECT id INTO repo_id FROM repository WHERE project_name=in_project_name;
    SET baseurl = CONCAT('./Users/', in_username, '/workbench/', repo_id, '/hotfix/');
    CALL grantAccessToRepo(in_username, repo_id);
    INSERT INTO hotfix_pri(hotfix_pub_id, username, url) VALUES(repo_id, in_username, baseurl);
    SELECT url, version INTO tempurl, tempversion  FROM hotfix_pub WHERE develop_id=repo_id;
    SET joinurl = CONCAT(tempurl, '.v_', tempversion, '/');
    SET feature_url = baseurl;
  END;
|

CREATE PROCEDURE createFeatureBranch(IN in_develop_id VARCHAR(40), IN in_name VARCHAR(40),
  IN in_notes TEXT, IN in_username VARCHAR(40), OUT devurl VARCHAR(250), OUT setupurl VARCHAR(250),
  OUT out_proj_name VARCHAR(40))
  BEGIN
    DECLARE temp VARCHAR(250);
    DECLARE dev_url VARCHAR(250);
    DECLARE dev_ver INTEGER;
    DECLARE proj_name VARCHAR(40);
    SELECT url, version INTO dev_url, dev_ver FROM develop WHERE master_id=in_develop_id;
    SET devurl = CONCAT(dev_url, '.v_', dev_ver);
    SELECT url, project_name INTO temp, proj_name FROM repository WHERE id=in_develop_id;
    SET setupurl = CONCAT(temp, 'feature_', in_name, '/');
    INSERT INTO feature_pub(develop_id, name, url, notes) VALUES(in_develop_id, in_name, setupurl, in_notes);
    CALL addLogRow(in_username, 'Create Branch', in_name);
    CALL grantAccessToRepo(in_username, in_develop_id);
    SET out_proj_name = proj_name;
  END;
|

CREATE PROCEDURE createReleaseBranch(IN in_develop_id VARCHAR(40), IN in_notes TEXT,
  IN in_username VARCHAR(40), OUT devurl VARCHAR(250), OUT setupurl VARCHAR(250),
  OUT out_proj_name VARCHAR(40))
  BEGIN
    DECLARE temp VARCHAR(250);
    DECLARE dev_url VARCHAR(250);
    DECLARE dev_ver INTEGER;
    DECLARE proj_name VARCHAR(40);
    SELECT url, version INTO dev_url, dev_ver FROM develop WHERE master_id=in_develop_id;
    SET devurl = CONCAT(dev_url, '.v_', dev_ver);
    SELECT url, project_name INTO temp, proj_name FROM repository WHERE id=in_develop_id;
    SET setupurl = CONCAT(temp, 'release/');
    INSERT INTO release_pub(develop_id, url, notes) VALUES(in_develop_id, setupurl, in_notes);
    CALL addLogRow(in_username, 'Create Branch', 'Release');
    CALL grantAccessToRepo(in_username, in_develop_id);
    SET out_proj_name = proj_name;
  END;
|

CREATE PROCEDURE createHotfixBranch(IN in_develop_id VARCHAR(40), IN in_notes TEXT,
  IN in_username VARCHAR(40), OUT masterurl VARCHAR(250), OUT setupurl VARCHAR(250),
  OUT out_proj_name VARCHAR(40))
  BEGIN
    DECLARE temp VARCHAR(250);
    DECLARE master_url VARCHAR(250);
    DECLARE master_ver INTEGER;
    DECLARE proj_name VARCHAR(40);
    SELECT url, version INTO master_url, master_ver FROM master WHERE repo_id=in_develop_id;
    SET masterurl = CONCAT(master_url, '.v_', master_ver);
    SELECT url, project_name INTO temp, proj_name FROM repository WHERE id=in_develop_id;
    SET setupurl = CONCAT(temp, 'hotfix/');
    INSERT INTO hotfix_pub(develop_id, url, notes) VALUES(in_develop_id, setupurl, in_notes);
    CALL addLogRow(in_username, 'Create Branch', 'Hotfix');
    CALL grantAccessToRepo(in_username, in_develop_id);
    SET out_proj_name = proj_name;
  END;
|

CREATE PROCEDURE approvePush(IN in_repo_id VARCHAR(40), IN in_name VARCHAR(40),
  OUT push_url VARCHAR(250), OUT out_version INT)
  BEGIN
    DECLARE new_version INT;
    DECLARE temp VARCHAR(250);
    IF in_name = 'Master' THEN
      SELECT 1 + version, url INTO new_version, temp FROM master WHERE repo_id=in_repo_id;
      UPDATE master SET version=new_version WHERE repo_id=in_repo_id;
    ELSEIF in_name = 'Develop' THEN
      SELECT 1 + version, url INTO new_version, temp FROM develop WHERE master_id=in_repo_id;
      UPDATE develop SET version=new_version WHERE master_id=in_repo_id;
    ELSEIF in_name = 'Release' THEN
      SELECT 1 + version, url INTO new_version, temp FROM release_pub WHERE develop_id=in_repo_id;
      UPDATE release_pub SET version=new_version WHERE develop_id=in_repo_id;
    ELSEIF in_name = 'Hotfix' THEN
      SELECT 1 + version, url INTO new_version, temp FROM hotfix_pub WHERE develop_id=in_repo_id;
      UPDATE hotfix_pub SET version=new_version WHERE develop_id=in_repo_id;
    ELSE
      SELECT 1 + version, url INTO new_version, temp FROM feature_pub WHERE develop_id=in_repo_id AND name=in_name;
      UPDATE feature_pub SET version=new_version WHERE develop_id=in_repo_id AND name=in_name;
    END IF;
    SET push_url = temp;
    SET out_version = new_version;
  END;
|

CREATE PROCEDURE refusePush(IN in_repo_id VARCHAR(40), IN in_name VARCHAR(40),
  OUT push_url VARCHAR(250))
  BEGIN
    DECLARE new_version INT;
    DECLARE temp VARCHAR(250);
    IF in_name = 'Master' THEN
      SELECT url INTO temp FROM master WHERE repo_id=in_repo_id;
    ELSEIF in_name = 'Develop' THEN
      SELECT url INTO temp FROM develop WHERE master_id=in_repo_id;
    ELSEIF in_name = 'Release' THEN
      SELECT url INTO temp FROM release_pub WHERE develop_id=in_repo_id;
    ELSEIF in_name = 'Hotfix' THEN
      SELECT url INTO temp FROM hotfix_pub WHERE develop_id=in_repo_id;
    ELSE
      SELECT url INTO temp FROM feature_pub WHERE develop_id=in_repo_id AND name=in_name;
    END IF;
    SET push_url = temp;
  END;
|

DELIMITER ;
