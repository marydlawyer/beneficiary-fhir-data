package gov.hhs.cms.bluebutton.data.model.rif;

/**
 * Models a single beneficiary/claim/drug event that was contained in a
 * {@link RifFile}. Please note that all lines/revenue centers for a single
 * claim will be grouped together into a single {@link RifRecordEvent} instance.
 * 
 * @param <R>
 *            the record type stored in this {@link RifRecordEvent}
 */
public final class RifRecordEvent<R> {
	private final RifFilesEvent filesEvent;
	private final RifFile file;
	private final RecordAction recordAction;
	private final R record;

	/**
	 * Constructs a new {@link RifRecordEvent} instance.
	 * 
	 * @param filesEvent
	 *            the value to use for {@link #getFilesEvent()}
	 * @param file
	 *            the value to use for {@link #getFile()}
	 * @param record
	 *            the value to use for {@link #getRecord()}
	 */
	public RifRecordEvent(RifFilesEvent filesEvent, RifFile file, RecordAction recordAction, R record) {
		if (filesEvent == null)
			throw new IllegalArgumentException();
		if (file == null)
			throw new IllegalArgumentException();
		if (recordAction == null)
			throw new IllegalArgumentException();
		if (record == null)
			throw new IllegalArgumentException();

		this.filesEvent = filesEvent;
		this.file = file;
		this.recordAction = recordAction;
		this.record = record;
	}

	/**
	 * @return the {@link RifFilesEvent} that this is a child of
	 */
	public RifFilesEvent getFilesEvent() {
		return filesEvent;
	}

	/**
	 * @return the specific {@link RifFile} that this {@link RifRecordEvent}'s
	 *         data is from
	 */
	public RifFile getFile() {
		return file;
	}

	/**
	 * @return the RIF {@link RecordAction} indicated for the
	 *         {@link #getRecord()}
	 */
	public RecordAction getRecordAction() {
		return recordAction;
	}

	/**
	 * @return the actual RIF data that the {@link RifRecordEvent} represents
	 */
	public R getRecord() {
		return record;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RifRecordEvent [filesEvent=");
		builder.append(filesEvent);
		builder.append(", file=");
		builder.append(file);
		builder.append(", recordAction=");
		builder.append(recordAction);
		builder.append(", record=");
		builder.append(record);
		builder.append("]");
		return builder.toString();
	}
}
