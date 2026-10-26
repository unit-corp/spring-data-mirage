-- QueryRepository_findFixed.sql

SELECT *
FROM samples

WHERE str IN (/*first*/'a', /*second*/'b')

ORDER BY id
