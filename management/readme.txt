
INSERT INTO Routes (start_location, end_location, distance_km, estimated_time_min, base_fare, per_km_rate, created_by) VALUES
-- 1-5: COMSATS Islamabad to Major Destinations
('COMSATS University Islamabad', 'G-10 Markaz Islamabad', 12.50, 25, 50.00, 15.00, 1),
('COMSATS University Islamabad', 'Faizabad Interchange', 18.00, 35, 50.00, 15.00, 1),
('COMSATS University Islamabad', 'Saddar Rawalpindi', 25.00, 45, 50.00, 15.00, 1),
('COMSATS University Islamabad', 'Islamabad Airport (ISB)', 22.50, 40, 50.00, 15.00, 1),
('COMSATS University Islamabad', 'Bahria Town Phase 4', 33.20, 55, 50.00, 15.00, 1),

-- 6-10: COMSATS to Educational & Government Institutes
('COMSATS University Islamabad', 'Quaid-e-Azam University', 8.50, 20, 50.00, 15.00, 1),
('COMSATS University Islamabad', 'NUST University H-12', 6.80, 15, 50.00, 15.00, 1),
('COMSATS University Islamabad', 'PIMS Hospital G-8', 14.20, 30, 50.00, 15.00, 1),
('COMSATS University Islamabad', 'Pak Secretariat G-5', 16.80, 35, 50.00, 15.00, 1),
('COMSATS University Islamabad', 'Faisal Mosque E-8', 11.50, 25, 50.00, 15.00, 1),

-- 11-15: Major Islamabad Sector Routes
('G-11 Markaz Islamabad', 'F-6 Super Market', 10.30, 22, 50.00, 15.00, 1),
('F-8 Markaz Islamabad', 'Blue Area Islamabad', 6.50, 15, 50.00, 15.00, 1),
('I-8 Markaz Islamabad', 'I-14 Sector Islamabad', 15.80, 30, 50.00, 15.00, 1),
('G-9 Markaz Islamabad', 'Pirwadhai Bus Stand', 12.20, 25, 50.00, 15.00, 1),
('H-12 COMSATS Campus', 'G-7 Markaz Islamabad', 15.80, 32, 50.00, 15.00, 1),

-- 16-20: Rawalpindi & Inter-City Routes
('Saddar Rawalpindi', 'Raja Bazaar Rawalpindi', 3.50, 10, 50.00, 15.00, 1),
('Faizabad Interchange', '6th Road Rawalpindi', 5.20, 12, 50.00, 15.00, 1),
('Committee Chowk Rawalpindi', 'Chandni Chowk Rawalpindi', 7.80, 18, 50.00, 15.00, 1),
('Rawalpindi Railway Station', 'Islamabad Zoo', 18.50, 35, 50.00, 15.00, 1),
('Margalla Road Islamabad', 'Murree Road Rawalpindi', 12.30, 28, 50.00, 15.00, 1);

INSERT INTO Routes (start_location, end_location, distance_km, estimated_time_min, base_fare, per_km_rate, created_by) VALUES
-- 21-25: Shopping Malls & Commercial Areas
('Centaurus Mall Islamabad', 'Giga Mall Islamabad', 8.50, 20, 50.00, 15.00, 1),
('Jinnah Super Market F-7', 'Jasmine Garden Islamabad', 6.20, 15, 50.00, 15.00, 1),
('Jinnah Park Rawalpindi', 'Bahria Town Phase 7', 9.80, 22, 50.00, 15.00, 1),
('Jinnah Avenue Blue Area', 'Kohsar Market F-6', 3.50, 8, 50.00, 15.00, 1),
('Gulberg Greens Islamabad', 'DHA Phase 2 Islamabad', 12.40, 25, 50.00, 15.00, 1),

-- 26-30: Hospitals & Healthcare Facilities
('Shifa International Hospital', 'Ali Medical Center F-8', 4.20, 10, 50.00, 15.00, 1),
('Rawalpindi General Hospital', 'Holy Family Hospital', 5.80, 15, 50.00, 15.00, 1),
('PIMS Hospital G-8', 'Polyclinic Hospital G-7', 3.50, 8, 50.00, 15.00, 1),
('Quaid-e-Azam International Hospital', 'Maroof Hospital F-10', 2.80, 7, 50.00, 15.00, 1),
('Benazir Bhutto Hospital', 'Hearts International Hospital', 6.50, 15, 50.00, 15.00, 1),


('Daman-e-Koh Viewpoint', 'Monal Restaurant Pir Sohawa', 15.20, 35, 50.00, 15.00, 1),
('Lake View Park Islamabad', 'Rawal Lake View Point', 8.50, 20, 50.00, 15.00, 1),
('Pakistan Monument Museum', 'Lok Virsa Museum', 1.50, 5, 50.00, 15.00, 1),
('Fatima Jinnah Park F-9', 'Rose and Jasmine Garden', 7.20, 15, 50.00, 15.00, 1),
('Ayub National Park Rawalpindi', 'Army Museum Rawalpindi', 4.80, 12, 50.00, 15.00, 1);

-- Premium Routes with Higher Rates
INSERT INTO Routes (start_location, end_location, distance_km, estimated_time_min, base_fare, per_km_rate, created_by, route_status) VALUES
-- Airport & VIP Routes
('Islamabad Airport (ISB)', 'Serena Hotel Islamabad', 18.50, 30, 100.00, 25.00, 1, 'Active'),
('Islamabad Airport (ISB)', 'Pearl Continental Rawalpindi', 22.80, 38, 100.00, 25.00, 1, 'Active'),
('Marriott Hotel Islamabad', 'Ramada Plaza Islamabad', 6.20, 15, 100.00, 25.00, 1, 'Active'),
('Diplomatic Enclave G-5', 'Embassy of USA Islamabad', 3.50, 8, 100.00, 25.00, 1, 'Active'),
('Prime Minister Office', 'Parliament House Islamabad', 2.20, 5, 100.00, 25.00, 1, 'Active');