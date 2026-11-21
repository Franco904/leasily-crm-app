-- Base pesada para testar listas simples com ORDER BY e EXPLAIN QUERY PLAN em banco local grande:
-- 20.000 Proprietários, 20.000 Imóveis (1 foto principal e 1 Preço cada), 20.000 Clientes,
-- 20.000 Atendimentos (1 Interação e 1 Imóvel enviado cada) e 20.000 Estadias. Bairros e cidades
-- se repetem (40 e 10 valores), o que dá seletividade realista às buscas.
--
-- APAGA os dados de domínio do banco base e gera os novos. Uso (via make seed SEED=heavy):
--   sqlite3 banco.db < scripts/seed/heavy-seed.sql    (a partir da raiz do repositório)
--
-- Depois, no Android Studio: App Inspection > Database Inspector > abra o banco e rode, por exemplo,
--   EXPLAIN QUERY PLAN SELECT id, neighborhood FROM properties WHERE is_deleted = 0 ORDER BY neighborhood;

.bail on
PRAGMA foreign_keys = ON;
BEGIN TRANSACTION;

CREATE TEMP TABLE seed_params AS SELECT
    20000 AS n_registros,
    730 AS window_days,                                    -- Estadias espalhadas ±window_days/2 em torno de hoje
    CAST(strftime('%s', 'now') AS INTEGER) * 1000 AS now_ms;

CREATE TEMP TABLE seed_neighborhoods(idx INTEGER PRIMARY KEY, name TEXT NOT NULL);
INSERT INTO seed_neighborhoods(idx, name) VALUES
    (0, 'Centro'), (1, 'Agronômica'), (2, 'Trindade'), (3, 'Itacorubi'), (4, 'Lagoa da Conceição'),
    (5, 'Campeche'), (6, 'Ingleses'), (7, 'Canasvieiras'), (8, 'Jurerê'), (9, 'Coqueiros'),
    (10, 'Estreito'), (11, 'Balneário'), (12, 'Capoeiras'), (13, 'Saco dos Limões'), (14, 'Armação'),
    (15, 'Pântano do Sul'), (16, 'Santo Antônio de Lisboa'), (17, 'Barra da Lagoa'), (18, 'Rio Tavares'),
    (19, 'Córrego Grande'), (20, 'Pantanal'), (21, 'João Paulo'), (22, 'Monte Verde'), (23, 'Cacupé'),
    (24, 'Sambaqui'), (25, 'Santinho'), (26, 'Daniela'), (27, 'Cachoeira do Bom Jesus'), (28, 'Vargem Grande'),
    (29, 'Ratones'), (30, 'Ponta das Canas'), (31, 'Praia Brava'), (32, 'Moçambique'), (33, 'Tapera'),
    (34, 'Ribeirão da Ilha'), (35, 'Costeira do Pirajubaé'), (36, 'Carianos'), (37, 'Itaguaçu'),
    (38, 'Abraão'), (39, 'Bom Abrigo');

CREATE TEMP TABLE seed_cities(idx INTEGER PRIMARY KEY, name TEXT NOT NULL);
INSERT INTO seed_cities(idx, name) VALUES
    (0, 'Florianópolis'), (1, 'São José'), (2, 'Palhoça'), (3, 'Biguaçu'), (4, 'Porto Alegre'),
    (5, 'Curitiba'), (6, 'São Paulo'), (7, 'Joinville'), (8, 'Blumenau'), (9, 'Chapecó');

CREATE TEMP TABLE seed_seq AS
    WITH RECURSIVE s(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM s, seed_params WHERE n < n_registros
    )
    SELECT n FROM s;

-- Limpeza (filhos primeiro)
DELETE FROM stay_transfers;
DELETE FROM stay_files;
DELETE FROM assistance_interactions;
DELETE FROM assistance_preferred_neighborhoods;
DELETE FROM assistance_property_characteristics;
DELETE FROM assistance_sent_properties;
DELETE FROM stays;
DELETE FROM assistances;
DELETE FROM property_prices;
DELETE FROM property_files;
DELETE FROM property_characteristics_properties;
DELETE FROM properties;
DELETE FROM property_owner_files;
DELETE FROM property_owners;
DELETE FROM lead_tenants;

-- Proprietários
INSERT INTO property_owners (id, create_date_time, update_date_time, name, phone, email, payment_key, is_deleted, contract_version)
SELECT
    n,
    now_ms - n * 60000,
    now_ms - n * 60000,
    'Proprietário ' || printf('%05d', n),
    '48' || printf('9%08d', n),
    'proprietario' || n || '@leasily.com',
    'chave-pix-' || n,
    0,
    1
FROM seed_seq, seed_params;

-- Imóveis (1 Proprietário por Imóvel)
INSERT INTO properties (
    id, create_date_time, update_date_time, neighborhood, street_address, cep_postal_code, number,
    address_line_2, parking_spot_number, parking_basement_level_number, hobby_box_number,
    bedrooms, en_suites, double_beds, single_beds, bathrooms, parking_spots, air_conditioners, max_guests,
    distance_to_sea, included_items, has_included_items, cleaning_service_tax, has_cleaning_service_included,
    observations, is_deleted, property_owner_id, property_type_id, property_sea_proximity_type_id, contract_version
)
SELECT
    n,
    now_ms - n * 60000,
    now_ms - n * 60000,
    (SELECT name FROM seed_neighborhoods WHERE idx = n % 40),
    'Rua ' || printf('%05d', (n * 7) % 9973),
    printf('88%06d', n % 1000000),
    n % 900 + 1,
    NULL, NULL, NULL, NULL,
    1 + n % 4, n % 2, 1 + n % 2, n % 3, 1 + n % 3, n % 3, n % 4, 2 + n % 8,
    NULL, NULL, 0, '80.00', 1,
    NULL,
    0,
    n,
    1 + n % 2,
    NULL,
    1
FROM seed_seq, seed_params;

INSERT INTO property_files (id, property_file_uri, is_main_picture, property_id)
SELECT n, 'content://seed/imovel/' || n || '.jpg', 1, n FROM seed_seq;

INSERT INTO property_prices (id, start_date, end_date, amount, property_id)
SELECT
    n,
    (now_ms / 86400000) * 86400000,
    (now_ms / 86400000 + 30) * 86400000,
    printf('%d.00', 200 + n % 800),
    n
FROM seed_seq, seed_params;

-- Clientes
INSERT INTO lead_tenants (id, create_date_time, update_date_time, tag, name, city, identity_document, phone, email, contact_origin, observations, is_deleted, contract_version)
SELECT
    n,
    now_ms - n * 60000,
    now_ms - n * 60000,
    CASE WHEN n % 20 = 0 THEN 'VIP' END,
    'Cliente ' || printf('%05d', n),
    (SELECT name FROM seed_cities WHERE idx = n % 10),
    printf('%011d', n),
    '48' || printf('9%08d', n),
    'cliente' || n || '@leasily.com',
    CASE n % 3 WHEN 0 THEN 'Instagram' WHEN 1 THEN 'Indicação' ELSE 'WhatsApp' END,
    NULL,
    0,
    1
FROM seed_seq, seed_params;

-- Atendimentos (1 por Cliente; metade finalizada)
INSERT INTO assistances (
    id, create_date_time, update_date_time, searched_start_date, searched_end_date, has_flexibility_for_period,
    max_accepted_nightly_rate, adults, kids, babies, animals, bedrooms, en_suites, double_beds, single_beds,
    bathrooms, parking_spots, distance_to_sea, observations, is_finished, is_deleted,
    property_sea_proximity_type_id, lead_tenant_id, contract_version
)
SELECT
    n,
    now_ms - n * 60000,
    now_ms - n * 60000,
    NULL, NULL, 0,
    NULL, 1 + n % 4, n % 3, 0, 0, 1 + n % 3, 0, 1, 0,
    1, 1, NULL, NULL, n % 2, 0,
    NULL, n, 1
FROM seed_seq, seed_params;

INSERT INTO assistance_interactions (id, date, title, description, assistance_id)
SELECT n, (now_ms / 86400000) * 86400000, 'Contato ' || n, NULL, n FROM seed_seq, seed_params;

INSERT INTO assistance_sent_properties (assistance_id, property_id)
SELECT n, (n * 7) % 20000 + 1 FROM seed_seq;

-- Estadias (1 por Cliente, em Imóveis variados; status 0 = aguardando check-in, 1 = em andamento, 2 = finalizada)
INSERT INTO stays (
    id, create_date_time, update_date_time, vehicle_make, vehicle_color, vehicle_license_plate, guests,
    check_in_date_time, check_out_date_time, is_auto_check_in, is_auto_check_out, total_price, commission,
    observations, is_deleted, status, property_id, tenant_id, contract_version
)
SELECT
    n,
    now_ms - n * 60000,
    now_ms - n * 60000,
    NULL, NULL, NULL, 1 + n % 6,
    now_ms + ((n % window_days) - window_days / 2) * 86400000,
    now_ms + ((n % window_days) - window_days / 2 + 3) * 86400000,
    NULL, NULL, printf('%d.00', 600 + n % 2400), printf('%d.00', 60 + n % 240),
    NULL, 0, n % 3, n, n, 1
FROM seed_seq, seed_params;

PRAGMA foreign_key_check;

COMMIT;

ANALYZE;
