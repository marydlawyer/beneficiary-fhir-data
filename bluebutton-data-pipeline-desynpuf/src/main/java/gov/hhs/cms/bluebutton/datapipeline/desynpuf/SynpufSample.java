package gov.hhs.cms.bluebutton.datapipeline.desynpuf;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents an extracted DE-SynPUF sample bundle.
 */
public final class SynpufSample {
	private final Path sampleDir;
	private final SynpufArchive archive;

	/**
	 * Constructs a new {@link SynpufSample}.
	 * 
	 * @param sampleDir
	 *            the directory that the files for this {@link SynpufSample} can
	 *            be found in
	 * @param archive
	 *            the value to use for {@link #getArchive()}
	 */
	public SynpufSample(Path sampleDir, SynpufArchive archive) {
		this.sampleDir = sampleDir;
		this.archive = archive;
	}

	/**
	 * @return the {@link SynpufArchive} that this {@link SynpufSample}
	 *         represents
	 */
	public SynpufArchive getArchive() {
		return archive;
	}

	/**
	 * @return the {@link Path}s to the extracted Beneficiary Summary files
	 */
	public Path[] getBeneficiarySummaries() {
		return new Path[] { sampleDir.resolve(SynpufFile.BENE_SUMMARY_2008.getFileName(archive)),
				sampleDir.resolve(SynpufFile.BENE_SUMMARY_2009.getFileName(archive)),
				sampleDir.resolve(SynpufFile.BENE_SUMMARY_2010.getFileName(archive)) };
	}

	/**
	 * @return the {@link Path} to the extracted Beneficiary Summary files
	 */
	public Path getPartAClaimsOutpatient() {
		return sampleDir.resolve(SynpufFile.PART_A_CLAIMS_OUTPATIENT.getFileName(archive));
	}

	/**
	 * @return <code>true</code> if all files for this {@link SynpufSample}
	 *         exist, <code>false</code> if they do not
	 */
	public boolean allFilesExist() {
		for (SynpufFile file : SynpufFile.values())
			if (!Files.exists(sampleDir.resolve(file.getFileName(archive))))
				return false;

		return true;
	}
}
