# Index on Event.enrollment: its foreign key makes every upsert or delete of an enrollment scan the whole Event table (ANDROSDK-2443)

CREATE INDEX event_enrollment ON Event(enrollment);
