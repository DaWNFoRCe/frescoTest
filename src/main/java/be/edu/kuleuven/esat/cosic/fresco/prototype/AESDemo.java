package be.edu.kuleuven.esat.cosic.fresco.prototype;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.Protocol;
import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.configuration.CmdLineUtil;
import dk.alexandra.fresco.framework.sce.SCE;
import dk.alexandra.fresco.framework.sce.SCEFactory;
import dk.alexandra.fresco.framework.sce.configuration.ProtocolSuiteConfiguration;
import dk.alexandra.fresco.framework.sce.configuration.SCEConfiguration;
import dk.alexandra.fresco.framework.util.ByteArithmetic;
import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.lib.crypto.BristolCryptoFactory;
import dk.alexandra.fresco.lib.field.bool.BasicLogicFactory;
import dk.alexandra.fresco.lib.helper.ParallelProtocolProducer;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;

public class AESDemo implements Application {
	private int myId;
	private boolean[] in;

	private OBool[] out;

	private final static int BLOCK_SIZE = 128; // We do 128 bit AES.

	public AESDemo(int myId, boolean[] input) {
		this.myId = myId;
		this.in = input;
	}

	public static void execute(String[] args) throws Exception {

		// Read FRESCO configuration from command line args.
		CmdLineUtil util = new CmdLineUtil();
		util.parse(args);
		SCEConfiguration sceConf = util.getSCEConfiguration();
		ProtocolSuiteConfiguration psConf = util
				.getProtocolSuiteConfiguration();

		// Read and parse key or plaintext.
		String in = util.getRemainingArgs()[0];
		boolean[] input = ByteArithmetic.toBoolean(in);

		// Run secure computation.
		AESDemo aes = new AESDemo(sceConf.getMyId(), input);
		SCE sce = SCEFactory.getSCEFromConfiguration(sceConf, psConf);
		sce.runApplication(aes);

		// Print result.
		boolean[] res = new boolean[BLOCK_SIZE];
		for (int i = 0; i < BLOCK_SIZE; i++) {
			res[i] = aes.out[i].getValue();
		}
		System.out.println("The resulting ciphertext is: "
				+ ByteArithmetic.toHex(res));

	}

	public ProtocolProducer prepareApplication(ProtocolFactory factory) {
		//the factory is select
		BasicLogicFactory blf = (BasicLogicFactory) factory;

		// Convert input to open FRESCO values.
		OBool[] plainOpen = new OBool[BLOCK_SIZE];
		OBool[] keyOpen = new OBool[BLOCK_SIZE];
		for (int i = 0; i < BLOCK_SIZE; i++) {
			keyOpen[i] = blf.getOBool();
			plainOpen[i] = blf.getOBool();
			if (this.myId == 1) {
				keyOpen[i].setValue(this.in[i]);
			} else if (this.myId == 2) {
				plainOpen[i].setValue(this.in[i]);
			}
			;
		}

		// Establish some secure values.
		SBool[] keyClosed = blf.getSBools(BLOCK_SIZE);
		SBool[] plainClosed = blf.getSBools(BLOCK_SIZE);
		SBool[] outClosed = blf.getSBools(BLOCK_SIZE);

		// Build protocol where Alice (id=1) closes his key.
		ProtocolProducer[] closeKeyBits = new ProtocolProducer[BLOCK_SIZE];
		for (int i = 0; i < BLOCK_SIZE; i++) {
			closeKeyBits[i] = blf.getCloseProtocol(1, keyOpen[i], keyClosed[i]);
		}
		ProtocolProducer closeKey = new ParallelProtocolProducer(closeKeyBits);

		// Build protocol where Bob (id=2) closes his plaintext.
		ProtocolProducer[] closePlainBits = new ProtocolProducer[BLOCK_SIZE];
		for (int i = 0; i < BLOCK_SIZE; i++) {
			closePlainBits[i] = blf.getCloseProtocol(2, plainOpen[i],
					plainClosed[i]);
		}
		ProtocolProducer closePlain = new ParallelProtocolProducer(
				closePlainBits);

		// We can close key and plaintext in parallel.
		ProtocolProducer closeKeyAndPlain = new ParallelProtocolProducer(
				closeKey, closePlain);

		// Build an AES protocol.
		Protocol doAES = new BristolCryptoFactory(blf).getAesProtocol(
				plainClosed, keyClosed, outClosed);

		// Create wires that glue together the AES to the following open of the
		// result.
		this.out = blf.getOBools(BLOCK_SIZE);

		// Construct protocol for opening up the result.
		Protocol[] opens = new Protocol[BLOCK_SIZE];
		for (int i = 0; i < BLOCK_SIZE; i++) {
			opens[i] = blf.getOpenProtocol(outClosed[i], out[i]);
		}
		ProtocolProducer openCipher = new ParallelProtocolProducer(opens);

		// First we close key and plaintext, then we do the AES, then we open
		// the resulting ciphertext.
		ProtocolProducer finalProtocol = new SequentialProtocolProducer(
				closeKeyAndPlain, doAES, openCipher);

		return finalProtocol;

	}

}
