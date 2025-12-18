-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM image where id < 0;
DELETE FROM horse where id < 0;
DELETE FROM owner where id < 0;

INSERT INTO owner (id, first_name, last_name, description)
VALUES (-1, 'Valentino', 'Lazarevic', 'e12223211@student.tuwiem.ac.at'),
       (-2, 'Alexandra', 'Ladislai', 'Experienced dressage trainer with over 15 years in competitive riding'),
       (-3, 'Karlo', 'Peranovic', 'Veterinarian specializing in equine medicine since 2010'),
       (-4, 'Philipp', 'Maurer', 'Horse breeder from a family with generations of equestrian tradition'),
       (-5, 'Jan Guenther', 'Giefing', 'Show jumping enthusiast and owner of multiple championship horses'),
       (-6, 'Leonardo', 'Lazarevic', 'Racing stable manager with expertise in thoroughbred training'),
       (-7, 'Lukas', 'Reif', 'Equine nutritionist who focuses on performance diet optimization'),
       (-8, 'Pamela', 'Lazarevic', 'Recreational rider who adopted several rescue horses'),
       (-9, 'Predrag', 'Lazarevic', 'Horse farm owner specializing in natural horsemanship techniques'),
       (-10, 'Max', 'Mustermann', 'a lot of text, so text and that text this text, that and this text, text text text text text text text text');

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id)
VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE', -1),
       (-2, 'Lucky', 'The lucky one!', '2000-10-22', 'MALE', -2),
       (-3, 'Bernie', 'The big one!', '2001-10-22', 'MALE', -3),
       (-4, 'Ernie', 'The funny one!', '2002-05-12', 'MALE', -4),
       (-5, 'Lucy', 'The brave one!', '2003-11-27', 'FEMALE', -4);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id)
VALUES (-6, 'Linda', 'The small one!', '2013-03-14', 'FEMALE', -3, -1, -2),
       (-7, 'Steve', 'The cute one!', '2014-01-10', 'MALE', -3, -1, -2),
       (-8, 'Justin', 'The shy one!', '2015-07-19', 'MALE', -3, -1, -2);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id)
VALUES (-9, 'Lucas', 'The gentle one!', '2016-09-19', 'MALE', -3, -6, -8),
       (-10, 'Larry', 'The playful one!', '2017-11-30', 'MALE', -3, -6, -7);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id)
VALUES (-11, 'Thunder', 'The powerful one!', '2005-04-18', 'MALE', -2),
       (-12, 'Daisy', 'The elegant one!', '2006-06-25', 'FEMALE', -1),
       (-13, 'Shadow', 'The mysterious one!', '2004-09-15', 'MALE', -3),
       (-14, 'Willow', 'The graceful one!', '2007-03-30', 'FEMALE', -4);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id)
VALUES (-15, 'Storm', 'The spirited one!', '2014-05-22', 'MALE', -2, -5, -13),
       (-16, 'Bella', 'The beautiful one!', '2015-08-11', 'FEMALE', -1, -12, -11),
       (-17, 'Apollo', 'The athletic one!', '2016-04-03', 'MALE', -3, -14, -3);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id)
VALUES (-18, 'Cinnamon', 'The spunky one!', '2018-07-12', 'FEMALE', -3, -16, -15),
       (-19, 'Prince', 'The noble one!', '2019-02-28', 'MALE', -1, -6, -17),
       (-20, 'Aurora', 'The radiant one!', '2019-11-05', 'FEMALE', -2, -16, -9);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id)
VALUES (-21, 'Phoenix', 'The rising star!', '2021-06-17', 'MALE', -4, -18, -19),
       (-22, 'Willow Jr', 'The promising one!', '2022-04-08', 'FEMALE', -3, -20, -17);