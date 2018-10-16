getColumnFuzzyMatches<-function(allTables, allColumns) {
  library(stringdist);
  library(data.table);
  
  uniqueColumnNames = unique(allColumns);
  distanceMatrix <-stringdistmatrix(uniqueColumnNames,uniqueColumnNames, method="jw", p=0.1);
  
  dimensions <- dim(distanceMatrix);
  distance_vector <- round(as.vector(distanceMatrix), 4);
  col1 <- rep(uniqueColumnNames, each=dimensions[2]);
  col2 <- rep(uniqueColumnNames, dimensions[1]);
  
  matching_frame <- as.data.table(as.data.frame(cbind(col1, col2, distance_vector)));
  matching_frame[,3] <- as.numeric(as.character(matching_frame$distance_vector));
  
  # merge back the table column names
  names(matching_frame) <- c('sourceCol', 'targetCol', 'distance');
  # create the table and column frame
  tableToCol <- data.table(allTables, allColumns);
  names(tableToCol) <- c('table', 'column');
  matching_frame <- merge(matching_frame,tableToCol, by.x='sourceCol', by.y='column', allow.cartesian=TRUE);
  names(matching_frame) <- c('sourceCol', 'targetCol', 'distance', 'sourceTable');
  matching_frame <- merge(matching_frame,tableToCol, by.x='targetCol', by.y='column', allow.cartesian=TRUE);
  names(matching_frame) <- c('sourceCol', 'targetCol', 'distance', 'sourceTable', 'targetTable');
  # ignore same table joins
  matching_frame <- matching_frame[targetTable != sourceTable];
  
  rm(allTables, allColumns, uniqueColumnNames, distanceMatrix, distance_vector);
  
  return(matching_frame);
}

