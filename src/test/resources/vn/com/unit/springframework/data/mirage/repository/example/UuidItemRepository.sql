-- UuidItemRepository.sql

SELECT *
FROM uuid_items

/*BEGIN*/
WHERE
	/*IF id != null*/
	AND id = /*id*/'abc'
	/*END*/
/*END*/

/*IF orders != null*/
ORDER BY /*$orders*/id
/*END*/

/*BEGIN*/
LIMIT
	/*IF offset != null*/
	/*offset*/0,
	/*END*/

	/*IF size != null*/
	/*size*/10
	/*END*/
/*END*/
