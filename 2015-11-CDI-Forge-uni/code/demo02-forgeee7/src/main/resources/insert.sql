INSERT INTO T_USER (id, version, name, email, login, password, role) VALUES (1000, 1, 'Antonio Goncalves', 'antonio.goncalves@gmail.com', 'agoncal', 'lEMDfl4CChEhCBULxNJf/3JdrUx5xz8p4zr/kqIAF9w=', 0);
INSERT INTO T_USER (id, version, name, email, login, password, role) VALUES (1001, 1, 'Antoine Sabot-Durand', 'antoine@sabot-durand.net', 'antoine_sd', 'mIe72tv4GOvC9jsbvm0xue2BG3e2vHvTffZub1FhpRI=', 0);
INSERT INTO T_USER (id, version, name, email, login, password, role) VALUES (1002, 1, 'Super Admin', 'super@admin.com', 'admin', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 1);
INSERT INTO T_USER (id, version, name, email, login, password, role) VALUES (1003, 1, 'Legacy Joe', 'joe@legacy.com', 'legacy', 'legacy', 1);


INSERT INTO T_CONFERENCE (id, version, name, country, city) VALUES (2000, 1, 'Devoxx BE', 'Belgium', 'Antwerp');
INSERT INTO T_CONFERENCE (id, version, name, country, city) VALUES (2001, 1, 'Devoxx FR', 'France', 'Paris');
INSERT INTO T_CONFERENCE (id, version, name, country, city) VALUES (2002, 1, 'Devoxx UK', 'United Kingdom', 'London');
INSERT INTO T_CONFERENCE (id, version, name, country, city) VALUES (2003, 1, 'Devoxx MA', 'Morroco', 'Casablanca');
INSERT INTO T_CONFERENCE (id, version, name, country, city) VALUES (2004, 1, 'Devoxx PL', 'Poland', 'Krakow');
INSERT INTO T_CONFERENCE (id, version, name, country, city) VALUES (2005, 1, 'JavaOne', 'USA', 'San Francisco');


INSERT INTO T_REIMBURSEMENT (id, version, currency, user_id, conference_id) VALUES (4000, 1, 0, 1000, 2000);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3000, 1, '3 nights at the Marriott', 123.45, 0, 0);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3001, 1, 'Diner with organizers', 234.56, 0, 1);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3002, 1, 'Train ticket from Paris to Antwerp', 345.67, 0, 2);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4000, 3000);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4000, 3001);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4000, 3002);


INSERT INTO T_REIMBURSEMENT (id, version, currency, user_id, conference_id) VALUES (4001, 1, 1, 1000, 2003);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3003, 1, '2 nights at the Ibis', 123.45, 0, 0);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3004, 1, 'Diner with friends', 234.56, 0, 1);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3005, 1, 'Diner on my own', 34.56, 0, 1);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3006, 1, 'Breakfast', 23.10, 0, 1);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3007, 1, 'Flight with Air France', 345.67, 0, 3);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4001, 3003);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4001, 3004);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4001, 3005);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4001, 3007);


INSERT INTO T_REIMBURSEMENT (id, version, currency, user_id, conference_id) VALUES (4002, 1, 0, 1001, 2005);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3008, 1, '5 nights at the Hilton', 987.45, 0, 0);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3009, 1, 'Diner with Red Hater', 234.56, 0, 1);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3010, 1, 'Breakfast', 34.56, 0, 1);
INSERT INTO T_EXPENSE (id, version, description, amount, currency, expenseType) VALUES (3011, 1, 'Flight with Air France', 1345.67, 0, 3);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4002, 3008);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4002, 3009);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4002, 3010);
INSERT INTO T_REIMBURSEMENT_T_EXPENSE (Reimbursement_id, expenses_id) VALUES (4002, 3011);
