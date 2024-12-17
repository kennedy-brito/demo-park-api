insert into tb_users (id, username, password, role)
    values
        (100, 'ana@gmail.com',  '$2a$12$uB64GhrQ63Zhx6pT6C/VzOrij/W2cMGRpnLb7vttXI2MBd35h9QW2', 'ROLE_ADMIN'),
        (101, 'bia@gmail.com',  '$2a$12$uB64GhrQ63Zhx6pT6C/VzOrij/W2cMGRpnLb7vttXI2MBd35h9QW2', 'ROLE_CLIENT'),
        (102, 'bob@gmail.com',  '$2a$12$uB64GhrQ63Zhx6pT6C/VzOrij/W2cMGRpnLb7vttXI2MBd35h9QW2', 'ROLE_CLIENT'),
        (103, 'toby@gmail.com', '$2a$12$uB64GhrQ63Zhx6pT6C/VzOrij/W2cMGRpnLb7vttXI2MBd35h9QW2', 'ROLE_CLIENT');

insert into tb_spots (id, code, status)
    values
        (10, 'A-01', 'FREE'),
        (20, 'A-02', 'FREE'),
        (30, 'A-03', 'OCCUPIED'),
        (40, 'A-04', 'FREE')
