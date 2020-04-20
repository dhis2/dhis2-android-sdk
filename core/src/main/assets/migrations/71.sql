# Create index to speed up common option queries
CREATE INDEX optionset_optioncode ON Option(optionSet, code);